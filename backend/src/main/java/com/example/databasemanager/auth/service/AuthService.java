package com.example.databasemanager.auth.service;

import com.example.databasemanager.auth.dto.LoginRequest;
import com.example.databasemanager.auth.dto.LoginResponse;

/** Handles user authentication and JWT token generation. */
public interface AuthService {

    /**
     * Validates credentials and returns a JWT token on success.
     *
     * @param request contains username and password
     * @return success with JWT token, or failure with error message
     */
    LoginResponse login(LoginRequest request);
}
