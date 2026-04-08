package com.etransaction.response;

import java.math.BigDecimal;

public record TransferNotificationResponse(
        String senderFirstName,
        String senderLastName,
        String senderEmail,
        Long senderAccountNumber,
        String receiverFirstName,
        String receiverLastName,
        String receiverEmail,
        Long receiverAccountNumber,
        BigDecimal transferAmount
) {
}
