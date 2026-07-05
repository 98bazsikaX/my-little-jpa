package com.example.databasemanager.user.dto;

import com.example.databasemanager.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/** DTO for {@link User} entity returned by the API. Date fields are UTC epoch milliseconds. */
@Getter
@Setter
@Builder
public class UserDto {

    private Long id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private Long lastLogin;
    private Long created;
    private Long updated;
}
