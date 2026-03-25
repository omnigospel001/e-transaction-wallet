package com.etransaction.service;


import com.etransaction.entity.User;
import com.etransaction.request.LoginRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    User register(UserRequest userRequest);
}
