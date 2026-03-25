package com.etransaction.controller;

import com.etransaction.entity.User;
import com.etransaction.request.LoginRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.LoginResponse;
import com.etransaction.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok().body(authService.register(userRequest));
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

}
