package com.contact.management.module.auth.domain;

import jakarta.validation.constraints.NotBlank;

public record LoginInfo(
        @NotBlank String username,
        @NotBlank String password) {
}
