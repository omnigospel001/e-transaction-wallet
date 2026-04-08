package com.etransaction.service;

import com.etransaction.entity.User;
import com.etransaction.request.UpdateUserRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    List<User> registerMany(List<UserRequest> userRequest);

    UserResponse updateUser(Authentication connectedUser, UpdateUserRequest updateUserRequest);

    UserResponse findById(Authentication connectedUser);

    Page<UserResponse> findAll(Integer page, Integer size);

    void delete(Authentication connectedUser);

}
