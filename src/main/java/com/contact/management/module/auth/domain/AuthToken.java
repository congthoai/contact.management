package com.contact.management.module.auth.domain;

public record AuthToken(
        String accessToken,
        String tokenType) {
}
