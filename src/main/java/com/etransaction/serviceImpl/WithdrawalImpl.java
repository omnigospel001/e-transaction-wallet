package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.exception.DuplicateTransferRequestException;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.mapper.WithdrawalMapper;
import com.etransaction.mapper.WithdrawalWalletMapper;
import com.etransaction.repository.TransactionHistoryRepository;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.WithdrawalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class WithdrawalImpl implements WithdrawalService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository historyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WalletRepository walletRepository;

    public WithdrawalImpl(UserRepository userRepository, TransactionHistoryRepository historyRepository, RedisTemplate<String, Object> redisTemplate, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.redisTemplate = redisTemplate;
        this.walletRepository =walletRepository;;
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest withdrawalRequest, String idempotencyKey, Long id) {

        String responseKey = "idem:transfer:" + idempotencyKey;
        String lockKey = responseKey + ":lock";

        TransactionResponse cached =
                (TransactionResponse) redisTemplate.opsForValue().get(responseKey);

        if (cached != null) {
            log.info("Returning cached response for key={}", idempotencyKey);
            return cached;
        }

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCK", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new DuplicateTransferRequestException(
                    "Duplicate request in progress. Please wait...");
        }

        try{
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        //withdrawal is done here
        walletRepository.save(WithdrawalWalletMapper.wallet(user, withdrawalRequest));

        //
        historyRepository.save(WithdrawalMapper.makePayment(user, withdrawalRequest));

        var transactionResponse = new TransactionResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                withdrawalRequest.getAmount(),
                "SELF_WITHDRAWAL"
        );

            redisTemplate.opsForValue()
                    .set(responseKey, transactionResponse, Duration.ofMinutes(5));

            return transactionResponse;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
