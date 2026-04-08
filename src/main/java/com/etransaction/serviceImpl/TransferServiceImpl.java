package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.exception.*;
import com.etransaction.kafka.NotificationProducer;
import com.etransaction.kafka.TransferNotificationRequest;
import com.etransaction.mapper.TransferMapper;
import com.etransaction.mapper.TransferWalletMapper;
import com.etransaction.repository.TransactionHistoryRepository;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.TransferRequest;
import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.TransferService;
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
public class TransferServiceImpl implements TransferService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final EntityManager entityManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationProducer notificationProducer;

    private static final BigDecimal MINIMUM_DEPOSIT = new BigDecimal("100");

    public TransferServiceImpl(UserRepository userRepository, TransactionHistoryRepository transactionHistoryRepository, WalletRepository walletRepository, EntityManager entityManager, RedisTemplate<String, Object> redisTemplate, NotificationProducer notificationProducer) {
        this.userRepository = userRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.walletRepository = walletRepository;
        this.entityManager = entityManager;
        this.redisTemplate = redisTemplate;
        this.notificationProducer = notificationProducer;
    }

    @Override
    @Transactional
    public TransactionResponse makePayment(Authentication connectedUser, String idempotencyKey, TransferRequest transactionRequest) {

        User user = ((User) connectedUser.getPrincipal());

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


        User userFount = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (transactionRequest.getAmount().compareTo(MINIMUM_DEPOSIT) <= 0) {
            throw new LowerAmountException("Amount must be greater than 100");
        }


        //I'm checking if the user is sending money to himself
        User userSender = YouCannotSendMoneyToYourself(userFount, transactionRequest);

        //Debiting is done here
        Query queryCount = entityManager.createNativeQuery("SELECT COUNT(user_id) FROM wallet WHERE user_id =:user_id");

        queryCount.setParameter("user_id", userSender.getId());

        long UserIdCount = (Long) queryCount.getSingleResult();

        // Remember BODMAS RULE (Bracket-> Order-> Division-> Multiplication-> Addition-> Subtraction)
        // Procedure, we have Open-Bracket-Multiplication then Summation -> Division -> Multiplied by the User-DB-Count, then Subtraction from main account balance
        Query query  =  entityManager.createNativeQuery("UPDATE wallet SET amount = amount - "+ transactionRequest.getAmount() +" / " + UserIdCount + " WHERE user_id =:user_id " );
        query.setParameter("user_id", user.getId());

        query.executeUpdate();
        log.info("DEBIT DONE ===========>");

        //I'm handling crediting here
        var walletForReceiver = userRepository.findByAccountNumber(transactionRequest.getAccountNumber()).orElseThrow(() -> new UserNotFoundException("No user with this account number is found"));

        var wallet = TransferWalletMapper.wallet(walletForReceiver, transactionRequest);

        walletRepository.save(wallet);
        log.info("CREDIT DONE ===========>");

        log.info("Both sender and receiver transactions have been committed");

        //Saving the transactions
        transactionHistoryRepository.save(TransferMapper.makePayment(userSender, transactionRequest));

        //I'm accounting for balance sufficiency
        insufficientBalance(user);

        //Feeding the kafka producer request and then sending out emails
        sendEmailAlerts(userSender, walletForReceiver, transactionRequest);

        return new TransactionResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                transactionRequest.getAmount(),
                transactionRequest.getRemark()
        );
    }


    //
    public User YouCannotSendMoneyToYourself(User user, TransferRequest transferRequest) {

        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (transferRequest.getAccountNumber().equals(userFound.getAccountNumber()))
        {
            throw new YouCannotSendMoneyToYourselfException("You Cannot Send Money To Yourself");
        }
        return userFound;

    }


    public User insufficientBalance(User user) {

        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        Query query  =  entityManager.createNativeQuery("SELECT SUM(amount) FROM wallet WHERE user_id =:user_id" );

        query.setParameter("user_id", userFound.getId());

        BigDecimal totalAmount = (BigDecimal) query.getSingleResult();

        BigDecimal LIMIT_DEPOSIT_AMOUNT = new BigDecimal("100.00");
        if (totalAmount.doubleValue() <= LIMIT_DEPOSIT_AMOUNT.doubleValue()){
            throw new InsufficientBalanceException("Insufficient balance");
        }

        return userFound;
    }


    public void sendEmailAlerts(User sender, User receiver, TransferRequest transferRequest) {

        this.notificationProducer.sendNotification(
                new TransferNotificationRequest(
                        sender.getFirstName(),
                        sender.getLastName(),
                        sender.getEmail(),
                        sender.getAccountNumber(),
                        receiver.getFirstName(),
                        receiver.getLastName(),
                        receiver.getEmail(),
                        receiver.getAccountNumber(),
                        transferRequest.getAmount()
                )
        );

    }


}


