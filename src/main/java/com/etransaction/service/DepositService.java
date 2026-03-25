package com.etransaction.service;

import com.etransaction.request.DepositRequest;
import com.etransaction.response.TransactionResponse;

public interface DepositService {

    TransactionResponse deposit(DepositRequest depositRequest, String idempotencyKey, Long id);
}
