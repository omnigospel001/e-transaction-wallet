package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.mapper.UserMapper;
import com.etransaction.repository.UserRepository;
import com.etransaction.request.UpdateUserRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import com.etransaction.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public List<User> registerMany(List<UserRequest> userRequests) {

        List<User> users = userRequests.stream()
                .map(UserMapper::createUser)
                .toList();

        return userRepository.saveAll(users);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#currentUser")
    public UserResponse updateUser(Authentication currentUser, UpdateUserRequest updateUserRequest) {

        User user = (User) currentUser.getPrincipal();

        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));

        //
        userFound.setFirstName(updateUserRequest.getFirstName());
        userFound.setLastName(updateUserRequest.getLastName());
        userFound.setEmail(updateUserRequest.getEmail());
        userFound.setPhone(updateUserRequest.getPhone());

        return UserMapper.updateUser(userFound);
    }

    @Override
    @Transactional
    @Cacheable(value = "users", key = "#connectedUser")
    public UserResponse findById(Authentication connectedUser) {
        User user = (User)connectedUser.getPrincipal();
        User found = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.userResponse(found);
    }

    @Override
    @Transactional
    @Cacheable(value = "users", key = "'all'")
    public Page<UserResponse> findAll(Integer page, Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserMapper::userResponse);

    }

    @Override
    @CacheEvict(value = "users", key = "#currentUser")
    public void delete(Authentication currentUser) {

        User user = (User) currentUser.getPrincipal();

        User userFound = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("No user not available to delete"));
        userRepository.delete(user);
        log.info("User with id: {} is deleted", userFound.getId());
    }
}
