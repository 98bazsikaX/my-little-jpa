package com.example.databasemanager.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateUserRequest {

    @NotBlank
    @Size(max = 128)
    private String userName;

    @NotBlank
    @Size(max = 128)
    @Email
    private String email;

    @Size(max = 128)
    @Builder.Default
    private String firstName;

    @Size(max = 128)
    @Builder.Default
    private String lastName;

    @NotBlank
    @Size(min = 8, max = 255)
    private String password;
}
