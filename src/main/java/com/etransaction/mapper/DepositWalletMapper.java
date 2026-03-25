package com.etransaction.mapper;

import com.etransaction.entity.User;
import com.etransaction.entity.Wallet;
import com.etransaction.enums.TransactionType;
import com.etransaction.request.DepositRequest;

import java.util.UUID;

public class DepositWalletMapper {

    public static Wallet wallet(User user, DepositRequest depositRequest) {
        if (depositRequest == null) throw new NullPointerException("Payload is empty");

        var wallet = new Wallet();
        wallet.setAmount(depositRequest.getAmount());
        wallet.setFirstName(user.getFirstName());
        wallet.setLastName(user.getLastName());
        wallet.setAccountNumber(user.getAccountNumber());
        wallet.setEmail(user.getEmail());
        wallet.setPhone(user.getPhone());
        wallet.setUser(user);
        wallet.setTransactionId(UUID.randomUUID().toString());
        wallet.setTransactionType(TransactionType.DEPOSIT);

        return wallet;
    }

}
