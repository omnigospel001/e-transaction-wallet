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
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        //
        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setEmail(updateUserRequest.getEmail());
        user.setPhone(updateUserRequest.getPhone());

        return UserMapper.updateUser(user);
    }

    @Override
    @Transactional
    @Cacheable(value = "users", key = "#id")
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.userResponse(user);
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
    @CacheEvict(value = "users", key = "#id")
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("No user not available to delete"));
        userRepository.delete(user);
        log.info("User with id: {} is deleted", id );
    }
}
