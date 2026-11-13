package com.contact.management.module.contact.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactForm(
        @NotBlank(message = "First name is required")
        @Size(max = 60, message = "First name must be at most 60 characters")
        String firstName,

        @Size(max = 60, message = "Last name must be at most 60 characters")
        String lastName,

        @Email(message = "Please enter a valid email address")
        @Size(max = 120, message = "Email must be at most 120 characters")
        String email,

        @Pattern(regexp = "^$|^[+0-9 ().-]{6,25}$", message = "Please enter a valid phone number")
        String phone,

        @Size(max = 100, message = "Company must be at most 100 characters")
        String company,

        @Size(max = 100, message = "Job title must be at most 100 characters")
        String jobTitle,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @Size(max = 2000, message = "Notes must be at most 2000 characters")
        String notes,

        @NotNull(message = "Category is required")
        Category category,

        boolean favorite) {
}
