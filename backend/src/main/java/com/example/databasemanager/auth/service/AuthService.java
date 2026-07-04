package com.example.databasemanager.auth.service;

import com.example.databasemanager.auth.dto.LoginRequest;
import com.example.databasemanager.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
