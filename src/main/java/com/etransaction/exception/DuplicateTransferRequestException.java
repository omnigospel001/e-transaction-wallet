package com.etransaction.exception;

public class DuplicateTransferRequestException extends RuntimeException {
    public DuplicateTransferRequestException(String message) {
        super(message);
    }
}
