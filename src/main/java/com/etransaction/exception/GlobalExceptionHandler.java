package com.etransaction.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(field, message);
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<?> userNotFoundException(UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                userNotFoundException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> requestCannotBeNullException(RequestCannotBeNullException requestCannotBeNullException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                requestCannotBeNullException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> lowerAmountException(LowerAmountException lowerAmountException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                lowerAmountException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> youCannotSendMoneyToYourselfException(YouCannotSendMoneyToYourselfException moneyToYourselfException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                moneyToYourselfException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> duplicateDepositRequestException(DuplicateDepositRequestException depositRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                depositRequestException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> insufficientBalanceException(InsufficientBalanceException insufficientBalanceException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                insufficientBalanceException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> insufficientBalanceException(DuplicateWithdrawalRequestException requestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                requestException.getMessage()
        );
    }

    @ExceptionHandler
    public ResponseEntity<?> invalidPasswordException(InvalidPasswordException passwordException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                passwordException.getMessage()
        );
    }
}
