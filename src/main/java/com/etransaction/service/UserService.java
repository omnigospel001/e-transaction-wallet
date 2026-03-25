package com.etransaction.service;

import com.etransaction.entity.User;
import com.etransaction.request.UpdateUserRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    List<User> registerMany(List<UserRequest> userRequest);

    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest);

    UserResponse findById(Long id);

    Page<UserResponse> findAll(Integer page, Integer size);

    void delete(Long id);

}
