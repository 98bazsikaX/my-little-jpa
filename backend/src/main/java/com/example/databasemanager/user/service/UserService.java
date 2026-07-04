package com.example.databasemanager.user.service;

import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.dto.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserDto> getAllUsers(Pageable pageable);

    Page<UserDto> queryUsers(UserFilter filter, Pageable pageable);

    UserDto createUser(CreateUserRequest request);

    void deleteUser(Long id);
}
