package com.etransaction.service;

import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;

public interface WithdrawalService {

    TransactionResponse withdraw(WithdrawalRequest withdrawalRequest, String idempotencyKey, Long id);
}