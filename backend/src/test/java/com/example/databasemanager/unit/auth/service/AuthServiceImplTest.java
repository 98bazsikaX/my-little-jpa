package com.example.databasemanager.unit.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.databasemanager.auth.dto.LoginRequest;
import com.example.databasemanager.auth.dto.LoginResponse;
import com.example.databasemanager.auth.service.AuthServiceImpl;
import com.example.databasemanager.security.JwtUtil;
import com.example.databasemanager.user.entity.User;
import com.example.databasemanager.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user = new User();
        user.setId(1L);
        user.setUserName("admin");
        user.setPassword(encoder.encode("1234"));
    }

    @Test
    void shouldLoginWithValidCredentials() {
        LoginRequest request =
                LoginRequest.builder().userName("admin").password("1234").build();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("admin")).thenReturn("test.jwt.token");

        LoginResponse response = authService.login(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Login successful");
        assertThat(response.getToken()).isEqualTo("test.jwt.token");
    }

    @Test
    void shouldFailLoginWithWrongPassword() {
        LoginRequest request =
                LoginRequest.builder().userName("admin").password("wrong").build();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(user));

        LoginResponse response = authService.login(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getToken()).isNull();
    }

    @Test
    void shouldFailLoginWithUnknownUser() {
        LoginRequest request =
                LoginRequest.builder().userName("unknown").password("1234").build();

        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        LoginResponse response = authService.login(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    void shouldUpdateLastLoginOnSuccess() {
        LoginRequest request =
                LoginRequest.builder().userName("admin").password("1234").build();

        when(userRepository.findByUserName("admin")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("admin")).thenReturn("token");

        authService.login(request);

        assertThat(user.getLastLogin()).isNotNull();
        verify(userRepository).save(user);
    }
}
