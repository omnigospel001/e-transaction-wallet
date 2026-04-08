package com.etransaction.kafka;

import com.etransaction.response.TransferNotificationResponse;
import com.etransaction.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferNotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "transfer-topic")
    public void sendTransferEmailNotificationToReceiver(TransferNotificationResponse transferNotificationResponse) {
        log.info(format("Consuming the message from transfer-topic Topic for Receiver:: %s", transferNotificationResponse));
        //
        emailService.sendTransferEmailNotificationToReceiver(transferNotificationResponse);
    }

    @KafkaListener(topics = "transfer-topic")
    public void sendTransferEmailNotificationToSender(TransferNotificationResponse transferNotificationResponse) {
        log.info(format("Consuming the message from transfer-topic Topic for Sender:: %s", transferNotificationResponse));
        //
        emailService.sendTransferEmailNotificationToSender(transferNotificationResponse);
    }

}
