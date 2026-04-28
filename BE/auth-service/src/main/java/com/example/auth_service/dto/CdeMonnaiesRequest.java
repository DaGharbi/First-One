package com.example.auth_service.dto;

public record CdeMonnaiesRequest(
        String codeAgence,
        String dateCdeMonnaies,
        Long p100M,
        Long p10M,
        Long p1Dn,
        Long p20M,
        Long p500M,
        Long p50M,
        Long p5M
) {
}
