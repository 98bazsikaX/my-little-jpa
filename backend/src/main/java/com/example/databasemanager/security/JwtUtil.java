package com.example.databasemanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Generates and validates HMAC-SHA JWT tokens. Secret and expiration are read
 * from {@code jwt.secret} and {@code jwt.expiration} application properties.
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
        this.expiration = expiration;
    }

    /**
     * Generates a signed JWT with the given username as subject.
     *
     * @param userName the subject claim value
     * @return compact JWT string
     */
    public String generateToken(String userName) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .subject(userName)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact();
    }

    /**
     * Extracts the subject (username) from a JWT token.
     *
     * @param token compact JWT string
     * @return the subject claim value
     */
    public String extractUserName(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validates the token signature and checks expiration.
     *
     * @param token compact JWT string
     * @return {@code true} if the token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
