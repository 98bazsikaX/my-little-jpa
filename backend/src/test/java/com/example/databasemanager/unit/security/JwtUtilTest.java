package com.example.databasemanager.unit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.databasemanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "dGVzdC1zZWNyZXQtZm9yLWp3dC10ZXN0aW5nLWluLW15LWxpdHRsZS1qcGE=",
                3600000);
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldExtractUserName() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(jwtUtil.extractUserName(token)).isEqualTo("testuser");
    }

    @Test
    void shouldValidateToken() {
        String token = jwtUtil.generateToken("testuser");
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtUtil shortLived = new JwtUtil(
                "dGVzdC1zZWNyZXQtZm9yLWp3dC10ZXN0aW5nLWluLW15LWxpdHRsZS1qcGE=",
                -1000);
        String token = shortLived.generateToken("testuser");
        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void shouldRejectMalformedToken() {
        assertThat(jwtUtil.validateToken("not.a.valid.token")).isFalse();
    }

    @Test
    void shouldRejectNullToken() {
        assertThat(jwtUtil.validateToken(null)).isFalse();
    }

    @Test
    void shouldRejectEmptyToken() {
        assertThat(jwtUtil.validateToken("")).isFalse();
    }
}
