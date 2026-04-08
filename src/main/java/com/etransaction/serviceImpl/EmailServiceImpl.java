package com.etransaction.serviceImpl;

import com.etransaction.response.TransferNotificationResponse;
import com.etransaction.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl  implements EmailService {

    private final JavaMailSender mailSender;

    private final EmailBody emailBody;

    @Value("${spring.company.name}")
    private String companyName;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.subject}")
    private String subjectEmail;


    @Async
    public void sendTransferEmailNotificationToReceiver(TransferNotificationResponse transferNotificationResponse) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom(senderEmail, companyName);
            messageHelper.setTo(transferNotificationResponse.receiverEmail());
            messageHelper.setSubject(subjectEmail);
            messageHelper.setText(emailBody.creditAlertEmailBody(transferNotificationResponse), true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Async
    public void sendTransferEmailNotificationToSender(TransferNotificationResponse transferNotificationResponse) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            var messageHelper = new MimeMessageHelper(message);

            messageHelper.setFrom(senderEmail, companyName);
            messageHelper.setTo(transferNotificationResponse.senderEmail());
            messageHelper.setSubject(subjectEmail);
            messageHelper.setText(emailBody.debitAlertEmailBody(transferNotificationResponse), true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

