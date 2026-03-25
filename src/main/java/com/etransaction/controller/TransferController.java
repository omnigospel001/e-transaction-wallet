package com.etransaction.controller;

import com.etransaction.request.TransferRequest;
import com.etransaction.response.TransactionResponse;
import com.etransaction.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer/{id}")
    public ResponseEntity<TransactionResponse> makePayment(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request,
            @PathVariable Long id) {

        return ResponseEntity.ok(
                transferService.makePayment(id, idempotencyKey, request)
        );
    }
}
