package com.etransaction.response;

public record UserResponse(
        String firstName,
        String lastName,
        Long accountNumber,
        String email,
        String phone)
{ }
