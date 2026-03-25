package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.entity.Wallet;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.exception.*;
import com.etransaction.mapper.TransferMapper;
import com.etransaction.mapper.TransferWalletMapper;
import com.etransaction.repository.TransactionHistoryRepository;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.TransferRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.TransferService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final EntityManager entityManager;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("100");

    public TransferServiceImpl(UserRepository userRepository, TransactionHistoryRepository transactionHistoryRepository, WalletRepository walletRepository, EntityManager entityManager, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.walletRepository = walletRepository;
        this.entityManager = entityManager;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public TransactionResponse makePayment(Long id, String idempotencyKey, TransferRequest transactionRequest) {

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

        if (transactionRequest.getAmount().compareTo(MINIMUM_DEPOSIT) <= 0)
        {
            throw new LowerAmountException("Amount must be greater than 100");
        }


        //I'm checking if the user is sending money to himself
        User userVerified = YouCannotSendMoneyToYourself(id, transactionRequest);

        //Checking if the user balance is sufficient for the transaction
        Query query = entityManager.createNativeQuery("SELECT SUM(amount) FROM wallet WHERE id =:id");
        query.setParameter("id", id);

        BigDecimal totalAmount = (BigDecimal) query.getSingleResult();

        if (totalAmount.doubleValue() <= MINIMUM_DEPOSIT.doubleValue()){

            throw new InsufficientBalanceException("Insufficient balance exception");
        }

        //debit is done here
        var walletForSender  = walletRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        walletForSender.setAmount(walletForSender.getAmount().subtract(transactionRequest.getAmount()));
        walletRepository.save(walletForSender);
        log.info("ONE");

        //I'm handling crediting here
        var walletForReceiver  = userRepository.findByAccountNumber(transactionRequest.getAccountNumber()).orElseThrow(() -> new UserNotFoundException("No user with this account number is found"));
        log.info("TWO");

        var wallet = TransferWalletMapper.wallet(walletForReceiver, transactionRequest);

        walletRepository.save(wallet);
        log.info("THREE");

        log.info("Both sender and receiver transactions have been committed");

        //
       var history = transactionHistoryRepository.save(TransferMapper.makePayment(userVerified, transactionRequest));

       log.info("FOUR:: {} ", history.getFirstName());
        var transactionResponse = new TransactionResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                transactionRequest.getAmount(),
                transactionRequest.getRemark()
        );


            redisTemplate.opsForValue()
                    .set(responseKey, transactionResponse, Duration.ofMinutes(5));

            return transactionResponse;
        } finally {
            redisTemplate.delete(lockKey);
        }


    }


    //
    public User YouCannotSendMoneyToYourself(Long id, TransferRequest transferRequest) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (transferRequest.getAccountNumber().equals(user.getAccountNumber()))
        {
            throw new YouCannotSendMoneyToYourselfException("You Cannot Send Money To Yourself");
        }
        return user;

    }
}


