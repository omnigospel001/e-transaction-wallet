package com.etransaction.service;

import com.etransaction.response.TransferNotificationResponse;

public interface EmailService {
    void sendTransferEmailNotificationToReceiver(TransferNotificationResponse notificationResponse);

    void sendTransferEmailNotificationToSender(TransferNotificationResponse notificationResponse);

}
