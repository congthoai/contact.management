package com.contact.management.module.contact.domain;

import java.time.Instant;

public record Contact(
        long id,
        String firstName,
        String lastName,
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

    public String fullName() {
        return lastName == null || lastName.isBlank()
                ? firstName
                : firstName + " " + lastName;
    }
}
