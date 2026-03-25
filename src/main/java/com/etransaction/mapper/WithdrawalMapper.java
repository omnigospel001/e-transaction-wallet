package com.etransaction.mapper;

import com.etransaction.entity.TransactionHistory;
import com.etransaction.entity.User;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.enums.TransactionType;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.request.DepositRequest;
import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;

import java.util.UUID;

public class WithdrawalMapper {

    public static TransactionHistory makePayment(User user, WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest == null) throw new NullPointerException("Payload is empty");

        return TransactionHistory.builder()
                .amount(withdrawalRequest.getAmount())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountNumber(user.getAccountNumber())
                .email(user.getEmail())
                .phone(user.getPhone())
                .user(user)
                .transactionId(UUID.randomUUID().toString())
                .remark("SELF_WITHDRAWAL")
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
    }

    public static TransactionResponse paymentResponse(User user, WithdrawalRequest withdrawalRequest) {
        if (user == null) throw new UserNotFoundException("Payload is empty");

        return new TransactionResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                withdrawalRequest.getAmount(),
                "SELF_WITHDRAWAL"
        );
    }
}
