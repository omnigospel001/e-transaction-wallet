package com.etransaction.exception;

public class DuplicateWithdrawalRequestException extends RuntimeException {
    public DuplicateWithdrawalRequestException(String message) {
        super(message);
    }
}
