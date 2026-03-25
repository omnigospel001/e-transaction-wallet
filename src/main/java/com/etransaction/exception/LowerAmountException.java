package com.etransaction.exception;

public class LowerAmountException extends RuntimeException {
    public LowerAmountException(String message) {
        super(message);
    }
}