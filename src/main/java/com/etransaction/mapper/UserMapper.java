package com.etransaction.mapper;

import com.etransaction.entity.User;
import com.etransaction.exception.RequestCannotBeNullException;
import com.etransaction.exception.UserNotFoundException;
import com.etransaction.repository.UserRepository;
import com.etransaction.request.UserRequest;
import com.etransaction.response.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UserMapper {

    private static PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserMapper(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        UserMapper.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    public static User createUser(UserRequest userRequest) {

        try {
            if (userRequest == null) throw new RequestCannotBeNullException("Request cannot be empty");

            return User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .accountNumber(generateAccountNumber())
                    .email(userRequest.getEmail())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .phone(userRequest.getPhone())
                    .build();
        } catch (RuntimeException | RequestCannotBeNullException e) {
            throw new RuntimeException(e);
        }

    }

    public static UserResponse updateUser(User user) {
        if (user == null) throw new UserNotFoundException("User is not found");

        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getEmail(),
                user.getPhone()
        );

    }

    public static UserResponse userResponse(User user) {
        if (user == null) throw new UserNotFoundException("User is not found");

        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getAccountNumber(),
                user.getEmail(),
                user.getPhone()
        );

    }

    public static long generateAccountNumber() {

        SecureRandom secureRandom = new SecureRandom();

        long upperBound = 1_000_000_000_0L;

        return secureRandom.nextLong(upperBound);

    }

}
