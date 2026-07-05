package com.example.databasemanager.auth.service;

import com.example.databasemanager.auth.dto.LoginRequest;
import com.example.databasemanager.auth.dto.LoginResponse;
import com.example.databasemanager.security.JwtUtil;
import com.example.databasemanager.user.entity.User;
import com.example.databasemanager.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository
            .findByUserName(request.getUserName())
            .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResponse.failure("Invalid username or password");
        }

        user.setLastLogin(LocalDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUserName());
        return LoginResponse.success("Login successful", token);
    }
}
