package com.contact.management.module.contact.domain;

import java.time.Instant;

public record ContactResponse(
        long id,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String company,
        String jobTitle,
        String address,
        String notes,
        Category category,
        boolean favorite,
        Instant createdAt,
        Instant updatedAt) {
}
