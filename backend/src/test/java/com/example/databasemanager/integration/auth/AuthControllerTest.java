package com.example.databasemanager.integration.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.databasemanager.user.dto.CreateUserRequest;
import com.example.databasemanager.user.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private Gson gson;

    @BeforeEach
    void setUp() {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName("logintest")
                .email("login@test.com")
                .password("password123")
                .build();
        try {
            userService.createUser(request);
        } catch (RuntimeException e) {
            // Already exists
        }
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        String body = """
                {"userName":"logintest","password":"password123"}""";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void shouldRejectLoginWithNonexistentUser() throws Exception {
        String body = """
                {"userName":"nobody","password":"somepassword"}""";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldRejectEmptyLoginRequest() throws Exception {
        String body = """
                {"userName":"","password":""}""";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
