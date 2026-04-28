package com.example.common.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.time.Instant;

@Service
public class JwtService {

    private final SecretKey SECRET = Keys.hmacShaKeyFor("my-secret-key-for-jwt-token-generation-must-be-long-enough".getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SECRET)
                .compact();
    }
}