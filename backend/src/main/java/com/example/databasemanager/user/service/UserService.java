package com.example.databasemanager.user.service;

import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.dto.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Business operations for {@link User} management. */
public interface UserService {

    /**
     * Returns a paginated list of all users.
     *
     * @param pageable pagination and sort parameters
     * @return page of user DTOs
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Queries users with optional filter criteria.
     *
     * @param filter   filter criteria built from {@link UserFilter}
     * @param pageable pagination and sort parameters
     * @return filtered page of user DTOs
     */
    Page<UserDto> queryUsers(UserFilter filter, Pageable pageable);

    /**
     * Creates a new user after validating uniqueness and encoding the password.
     *
     * @param request new user details
     * @return created user DTO
     * @throws com.example.databasemanager.common.exception.DuplicateResourceException if username or email is taken
     */
    UserDto createUser(CreateUserRequest request);

    /**
     * Deletes a user by ID.
     *
     * @param id user ID
     * @throws jakarta.persistence.EntityNotFoundException if user does not exist
     */
    void deleteUser(Long id);
}
