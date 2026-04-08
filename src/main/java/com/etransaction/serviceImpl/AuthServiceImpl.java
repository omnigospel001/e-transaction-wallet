package com.etransaction.serviceImpl;

import com.etransaction.entity.User;
import com.etransaction.exception.InvalidPasswordException;
import com.etransaction.exception.UserWithEmailAlreadyExistException;
import com.etransaction.exception.UserWithPhoneAlreadyExistException;
import com.etransaction.mapper.UserMapper;
import com.etransaction.repository.UserRepository;
import com.etransaction.repository.WalletRepository;
import com.etransaction.request.LoginRequest;
import com.etransaction.request.UserRequest;
import com.etransaction.response.LoginResponse;
import com.etransaction.security.JwtProvider;
import com.etransaction.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;


    public AuthServiceImpl(UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder,
                           JwtProvider jwtProvider,
                           AuthenticationManager authenticationManager
                           ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches){
            System.out.println("Password does not match");
            throw new InvalidPasswordException("Bad Credentials, Try again");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtProvider.generateToken(user.getEmail());
        return LoginResponse.builder().token(token).build();
    }

    @Override
    public User register(UserRequest userRequest) {

        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new UserWithEmailAlreadyExistException("User with this email already exist");
        }

        if (userRepository.findByEmail(userRequest.getPhone()).isPresent()) {
            throw new UserWithPhoneAlreadyExistException("User with this phone number already exist");
        }

        return userRepository.save(UserMapper.createUser(userRequest));
    }


}
