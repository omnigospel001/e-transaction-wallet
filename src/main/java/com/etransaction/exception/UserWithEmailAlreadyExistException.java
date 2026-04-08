package com.etransaction.exception;

public class UserWithEmailAlreadyExistException extends RuntimeException {
    public UserWithEmailAlreadyExistException(String message) {
        super(message);
    }
}
