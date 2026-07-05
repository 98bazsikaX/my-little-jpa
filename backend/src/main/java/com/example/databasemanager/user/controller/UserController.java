package com.example.databasemanager.user.controller;

import com.example.databasemanager.common.filter.FilterRequest;
import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for {@code /api/users}. Supports listing, filtered search,
 * creation, and deletion of users. Protected by {@link com.example.databasemanager.security.JwtFilter}.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns a paginated list of all users.
     *
     * @param pageable pagination and sort parameters (default size=10, sort=userName ASC)
     * @return page of user DTOs
     */
    @GetMapping
    public Page<UserDto> getAllUsers(
        @PageableDefault(size = 10, sort = "userName", direction = Sort.Direction.ASC) Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    /**
     * Searches users with optional filter criteria and pagination.
     *
     * @param filterRequest generic filter request with list of criteria
     * @param pageable      pagination and sort parameters
     * @return filtered page of user DTOs
     */
    @PostMapping("/search")
    public Page<UserDto> queryUsers(
        @RequestBody(required = false) FilterRequest filterRequest,
        @PageableDefault(size = 10, sort = "userName", direction = Sort.Direction.ASC) Pageable pageable) {
        FilterRequest fr = filterRequest != null ? filterRequest : new FilterRequest();
        return userService.queryUsers(fr, pageable);
    }

    /**
     * Creates a new user. Validates uniqueness of username and email.
     *
     * @param request new user details including password
     * @return the created user DTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    /**
     * Deletes a user by ID. Throws 404 if not found.
     *
     * @param id the user ID to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
