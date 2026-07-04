package com.example.databasemanager.user.controller;

import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.dto.UserFilter;
import com.example.databasemanager.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDto> getAllUsers(
            @PageableDefault(size = 10, sort = "userName", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PostMapping("/search")
    public Page<UserDto> queryUsers(
            @RequestBody(required = false) UserFilter filter,
            @PageableDefault(size = 10, sort = "userName", direction = Sort.Direction.ASC) Pageable pageable) {
        UserFilter f = filter != null ? filter : UserFilter.builder().build();
        return userService.queryUsers(f, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
