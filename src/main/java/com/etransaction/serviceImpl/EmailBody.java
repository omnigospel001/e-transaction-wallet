package com.etransaction.serviceImpl;

import com.etransaction.response.TransferNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
@RequiredArgsConstructor
public class EmailBody {

    LocalDate localDate = LocalDate.now();
    String formattedDate = localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));

    LocalTime localTime = LocalTime.now();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
    String formattedTime = timeFormatter.format(localTime);

    @Value("${spring.company.name}")
    private String companyName;


//    public String depositCreditAlertEmailBody(DepositNotificationResponse depositNotificationResponse){
//
//        String mailContent = "<p> Credit Alert On: "+ depositNotificationResponse.accountNumber() + " </p>"+
//                "<p> Amount: "+"USD "+depositNotificationResponse.depositAmount()+" CR"+"</p>" +
//                "<p> Reference: "+depositNotificationResponse.firstName()+" "+depositNotificationResponse.lastName()+""+"</p>" +
//                "<p> Date & Time: "+formattedDate+"  "+formattedTime+"</p>"+
//                "<p> Thank you</p> <br> "+companyName+"";
//
//        return mailContent;
//
//    }
//
//    public String withdrawalDebitAlertEmailBody(WithdrawalNotificationResponse withdrawalNotificationResponse){
//
//        String mailContent = "<p> Debit Alert On: "+ withdrawalNotificationResponse.accountNumber()+ " </p>"+
//                "<p> Amount: "+"USD "+withdrawalNotificationResponse.withdrawalAmount()+" DR"+"</p>" +
//                "<p> Reference: "+withdrawalNotificationResponse.firstName()+" "+withdrawalNotificationResponse.lastName()+""+"</p>" +
//                "<p> Date & Time: "+formattedDate+"  "+formattedTime+"</p>"+
//                "<p> Thank you</p> <br> "+companyName+"";
//
//        return mailContent;
//
//    }


    public String creditAlertEmailBody(TransferNotificationResponse transferNotificationResponse){

        String mailContent = "<p> Credit Alert On: "+ transferNotificationResponse.receiverAccountNumber() + " </p>"+
                "<p> Amount: "+"USD "+transferNotificationResponse.transferAmount()+" CR"+"</p>" +
                "<p> Sender: "+transferNotificationResponse.senderFirstName()+" "+transferNotificationResponse.senderLastName()+" </p>"+
                "<p> Date & Time: "+formattedDate+"  "+formattedTime+"</p>"+
                "<p> Thank you</p> <br> "+companyName+"";

        return mailContent;

    }


    public String debitAlertEmailBody(TransferNotificationResponse transferNotificationResponse){

        String mailContent = "<p> Debit Alert On: "+ transferNotificationResponse.senderAccountNumber() + " </p>"+
                "<p> Amount: "+"USD "+transferNotificationResponse.transferAmount()+" DR"+"</p>" +
                "<p> Recipient: "+transferNotificationResponse.senderFirstName()+" "+transferNotificationResponse.receiverLastName()+""+"</p>" +
                "<p> Date & Time: "+formattedDate+"  "+formattedTime+"</p>"+
                "<p> Thank you</p> <br> "+companyName+"";

        return mailContent;

    }
}
