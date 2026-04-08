package com.etransaction.service;

import com.etransaction.request.DepositRequest;
import com.etransaction.response.TransactionResponse;
import org.springframework.security.core.Authentication;

public interface DepositService {

    TransactionResponse deposit(DepositRequest depositRequest, String idempotencyKey, Authentication currentUser);
}
