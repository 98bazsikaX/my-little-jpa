package com.example.databasemanager.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserDto {

    private Long id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime lastLogin;
    private LocalDateTime created;
    private LocalDateTime updated;
}
