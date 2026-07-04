package com.example.databasemanager.unit.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.databasemanager.common.exception.DuplicateResourceException;
import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.dto.UserDto;
import com.example.databasemanager.user.entity.User;
import com.example.databasemanager.user.mapper.UserMapper;
import com.example.databasemanager.user.repository.UserRepository;
import com.example.databasemanager.user.service.UserServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded");

        userDto = UserDto.builder()
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .build();

        createRequest = CreateUserRequest.builder()
                .userName("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Test
    void shouldReturnPaginatedUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);
        when(userMapper.toDtoList(List.of(user))).thenReturn(List.of(userDto));

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserName()).isEqualTo("testuser");
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(createRequest);

        assertThat(result.getUserName()).isEqualTo("testuser");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).startsWith("$2a$");
    }

    @Test
    void shouldThrowOnDuplicateUserName() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Username already taken");
    }

    @Test
    void shouldThrowOnDuplicateEmail() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already taken");
    }

    @Test
    void shouldDeleteExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowOnDeleteNonexistentUser() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
