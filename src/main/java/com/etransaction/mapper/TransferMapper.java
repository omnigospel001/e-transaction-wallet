package com.etransaction.mapper;


import com.etransaction.entity.TransactionHistory;
import com.etransaction.entity.User;
import com.etransaction.enums.TransactionStatus;
import com.etransaction.enums.TransactionType;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.request.TransferRequest;
import com.etransaction.response.TransactionResponse;

import java.util.UUID;

public class TransferMapper {

    public static TransactionHistory makePayment(User user, TransferRequest transferRequest) {
        if (transferRequest == null) throw new NullPointerException("Payload is empty");

        return TransactionHistory.builder()
                .amount(transferRequest.getAmount())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .accountNumber(user.getAccountNumber())
                .email(user.getEmail())
                .phone(user.getPhone())
                .user(user)
                .transactionId(UUID.randomUUID().toString())
                .remark(transferRequest.getRemark())
                .transactionType(TransactionType.TRANSFER)
                .build();
    }

    public static TransactionResponse paymentResponse(User user, TransferRequest transferRequest) {
        if (user == null) throw new UserNotFoundException("Payload is empty");

        return new TransactionResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getPhone(),
                TransactionStatus.PAYMENT_SUCCESSFUL,
                UUID.randomUUID().toString(),
                transferRequest.getAmount(),
                transferRequest.getRemark()
        );
    }
}

