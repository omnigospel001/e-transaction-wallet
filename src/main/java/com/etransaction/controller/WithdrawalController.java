package com.etransaction.controller;

import com.etransaction.request.WithdrawalRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> makePayment(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                           @Valid @RequestBody WithdrawalRequest withdrawalRequest,
                                                           Authentication connectedUser) {


        return ResponseEntity.ok().body(withdrawalService.withdraw(withdrawalRequest, idempotencyKey, connectedUser));
    }
}
