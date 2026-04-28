package com.example.auth_service.dto;

public record LoginResponse(
        String token,
        AuthenticatedUserInfo user
) {
}
