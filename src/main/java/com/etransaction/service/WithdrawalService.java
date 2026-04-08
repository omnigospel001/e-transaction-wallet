package com.etransaction.service;

import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;
import org.springframework.security.core.Authentication;

public interface WithdrawalService {

    TransactionResponse withdraw(WithdrawalRequest withdrawalRequest, String idempotencyKey, Authentication connectedUser);
}