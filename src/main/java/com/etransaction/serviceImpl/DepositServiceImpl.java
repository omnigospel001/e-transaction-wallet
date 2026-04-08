package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.exception.DuplicateTransferRequestException;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.mapper.DepositMapper;
import com.etransaction.mapper.DepositWalletMapper;
import com.etransaction.repository.TransactionHistoryRepository;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.DepositRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.DepositService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class DepositServiceImpl implements DepositService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository historyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WalletRepository walletRepository;


    public DepositServiceImpl(UserRepository userRepository, TransactionHistoryRepository historyRepository, RedisTemplate<String, Object> redisTemplate, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.redisTemplate = redisTemplate;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest depositRequest, String idempotencyKey, Authentication currentUser) {

        User user = (User) currentUser.getPrincipal();

        String responseKey = "idem:transfer:" + idempotencyKey;
        String lockKey = responseKey + ":lock";

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCK", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new DuplicateTransferRequestException(
                    "Duplicate request in progress. Please wait...");
        }

        //
        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        //
        walletRepository.save(DepositWalletMapper.wallet(userFound, depositRequest));

        //
        historyRepository.save(DepositMapper.makePayment(userFound, depositRequest));

        return new TransactionResponse(
                userFound.getFirstName(),
                userFound.getLastName(),
                userFound.getAccountNumber(),
                userFound.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                depositRequest.getAmount(),
                "SELF_DEPOSIT"
        );
    }

}
