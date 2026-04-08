package com.etransaction.service;

import com.etransaction.request.TransferRequest;
import com.etransaction.response.TransactionResponse;
import org.springframework.security.core.Authentication;

public interface TransferService {

    TransactionResponse makePayment(Authentication connectedUser, String idempotencyKey, TransferRequest transferRequest);
}
