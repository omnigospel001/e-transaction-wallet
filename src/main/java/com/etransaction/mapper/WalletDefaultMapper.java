package com.etransaction.mapper;

import com.etransaction.entity.User;
import com.etransaction.entity.Wallet;
import com.etransaction.enums.TransactionType;
import com.etransaction.exception.UserNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletDefaultMapper {

    //
    // private static final BigDecimal DEFAULT_BALANCE = BigDecimal.ZERO;

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("50000");


    public static Wallet wallet(User user) {
        if (user == null) throw new UserNotFoundException("User not found");

        var wallet = new Wallet();
        wallet.setFirstName(user.getFirstName());
        wallet.setLastName(user.getLastName());
        wallet.setAccountNumber(user.getAccountNumber());
        wallet.setAmount(DEFAULT_BALANCE);
        wallet.setEmail(user.getEmail());
        wallet.setPhone(user.getPhone());
        wallet.setUser(user);
        wallet.setTransactionId(UUID.randomUUID().toString());
        wallet.setTransactionType(TransactionType.DEPOSIT);

        return wallet;
    }
}
