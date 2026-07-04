package com.example.databasemanager.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private final boolean success;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String token;

    public static LoginResponse failure(String message) {
        return LoginResponse.builder().success(false).message(message).build();
    }

    public static LoginResponse success(String message, String token) {
        return LoginResponse.builder()
                .success(true)
                .message(message)
                .token(token)
                .build();
    }
}
