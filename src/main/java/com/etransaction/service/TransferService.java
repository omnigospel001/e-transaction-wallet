package com.etransaction.service;

import com.etransaction.request.TransferRequest;
import com.etransaction.response.TransactionResponse;

public interface TransferService {

    TransactionResponse makePayment(Long id, String idempotencyKey, TransferRequest transferRequest);
}
