package com.etransaction.controller;

import com.etransaction.entity.User;
import com.etransaction.request.UpdateUserRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import com.etransaction.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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


    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest,
                                                   @PathVariable Long id) {

        return ResponseEntity.ok().body(userService.updateUser(id, updateUserRequest));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {

        return ResponseEntity.ok().body(userService.findById(id));
    }


    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<UserResponse> users = userService.findAll(page, size);

        return ResponseEntity.ok(users);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

}
