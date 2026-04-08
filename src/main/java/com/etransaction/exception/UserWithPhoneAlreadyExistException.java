package com.etransaction.exception;

public class UserWithPhoneAlreadyExistException extends RuntimeException {
    public UserWithPhoneAlreadyExistException(String message) {
        super(message);
    }
}
