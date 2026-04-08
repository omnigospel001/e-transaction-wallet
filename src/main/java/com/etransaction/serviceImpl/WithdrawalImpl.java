package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.entity.Wallet;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.exception.DuplicateTransferRequestException;
import com.etransaction.exception.InsufficientBalanceException;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.mapper.WithdrawalMapper;
import com.etransaction.mapper.WithdrawalWalletMapper;
import com.etransaction.repository.TransactionHistoryRepository;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.WithdrawalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class WithdrawalImpl implements WithdrawalService {

    private final UserRepository userRepository;
    private final TransactionHistoryRepository historyRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WalletRepository walletRepository;
    private final EntityManager entityManager;


    public WithdrawalImpl(UserRepository userRepository, TransactionHistoryRepository historyRepository, RedisTemplate<String, Object> redisTemplate, WalletRepository walletRepository, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.redisTemplate = redisTemplate;
        this.walletRepository =walletRepository;;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest withdrawalRequest, String idempotencyKey, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();

        String responseKey = "idem:transfer:" + idempotencyKey;
        String lockKey = responseKey + ":lock";

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCK", Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(lockAcquired)) {
            throw new DuplicateTransferRequestException(
                    "Duplicate request in progress. Please wait...");
        }

        //
        User useFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Query queryCount = entityManager.createNativeQuery("SELECT COUNT(user_id) FROM wallet WHERE user_id =:user_id");

        queryCount.setParameter("user_id", useFound.getId());

        long UserIdCount = (Long) queryCount.getSingleResult();

        // Remember BODMAS RULE (Bracket-> Order-> Division-> Multiplication-> Addition-> Subtraction)
        // Procedure, we have Open-Bracket-Multiplication then Summation -> Division -> Multiplied by the User-DB-Count, then Subtraction from main account balance
        Query query  =  entityManager.createNativeQuery("UPDATE wallet SET amount = amount - "+ withdrawalRequest.getAmount() +" / " + UserIdCount + " WHERE user_id =:user_id " );

        query.setParameter("user_id", useFound.getId());

        query.executeUpdate();


        //I'm accounting for balance sufficiency
        insufficientBalance(user, withdrawalRequest);

        //
        historyRepository.save(WithdrawalMapper.makePayment(useFound, withdrawalRequest));

        return new TransactionResponse(
                useFound.getFirstName(),
                useFound.getLastName(),
                useFound.getAccountNumber(),
                useFound.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                withdrawalRequest.getAmount(),
                "SELF_WITHDRAWAL"
        );
    }

    public User insufficientBalance(User user, WithdrawalRequest withdrawalRequest) {

        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Query query  =  entityManager.createNativeQuery("SELECT SUM(amount) FROM wallet WHERE user_id =:user_id" );

        query.setParameter("user_id", userFound.getId());

        BigDecimal totalAmount = (BigDecimal) query.getSingleResult();

        //BigDecimal LIMIT_DEPOSIT_AMOUNT = new BigDecimal("100.00");
        if (totalAmount.doubleValue() <= withdrawalRequest.getAmount().doubleValue()){
            throw new InsufficientBalanceException("Insufficient balance");
        }

      return userFound;
    }
}
