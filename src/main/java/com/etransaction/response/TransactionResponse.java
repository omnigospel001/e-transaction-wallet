package com.etransaction.response;

import com.etransaction.enums.TransactionStatus;

import java.math.BigDecimal;

public record TransactionResponse(
        String firstName,
        String lastName,
        Long accountNumber,
        String phone,
        TransactionStatus status,
        String transactionId,
        BigDecimal amount,
        String remark) {
}
