package com.example.databasemanager.auth.controller;

import com.example.databasemanager.auth.dto.LoginRequest;
import com.example.databasemanager.auth.dto.LoginResponse;
import com.example.databasemanager.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public authentication endpoint. Not protected by {@link com.example.databasemanager.security.JwtFilter}.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param request contains username and password, validated
     * @return a {@link LoginResponse} with JWT token on success, or error message on failure
     */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
