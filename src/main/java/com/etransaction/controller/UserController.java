package com.etransaction.controller;

import com.etransaction.entity.User;
import com.etransaction.request.UpdateUserRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import com.etransaction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/signup/many")
    public ResponseEntity<List<User>> registerMany(@Valid @RequestBody List<UserRequest> userRequest) {

        return ResponseEntity.ok().body(userService.registerMany(userRequest));
    }


    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                   Authentication connectedUser) {

        return ResponseEntity.ok().body(userService.updateUser(connectedUser, updateUserRequest));
    }

    @GetMapping("/find")
    public ResponseEntity<UserResponse> findById(@PathVariable Authentication connectedUser) {

        return ResponseEntity.ok().body(userService.findById(connectedUser));
    }


    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<UserResponse> users = userService.findAll(page, size);

        return ResponseEntity.ok(users);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@PathVariable Authentication connectedUser) {
        userService.delete(connectedUser);
        return ResponseEntity.ok().build();
    }

}
