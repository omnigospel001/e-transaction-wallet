package com.etransaction.controller;

import com.etransaction.request.DepositRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.DepositService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> makePayment(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                                           @Valid @RequestBody DepositRequest depositRequest,
                                                           Authentication connectedUser) {


        return ResponseEntity.ok().body(depositService.deposit(depositRequest, idempotencyKey, connectedUser));
    }
}
