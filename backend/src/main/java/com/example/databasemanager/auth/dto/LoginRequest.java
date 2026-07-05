package com.example.databasemanager.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** Request body for {@code POST /api/auth/login}. */
@Getter
@Setter
@Builder
public class LoginRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;
}
