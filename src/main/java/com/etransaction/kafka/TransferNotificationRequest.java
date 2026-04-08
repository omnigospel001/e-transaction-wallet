package com.etransaction.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferNotificationRequest(
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
