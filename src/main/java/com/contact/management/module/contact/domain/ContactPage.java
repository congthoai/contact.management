package com.contact.management.module.contact.domain;

import java.util.List;

public record ContactPage(
        List<ContactResponse> content,
        Page page) {

    public ContactPage {
        content = List.copyOf(content);
    }

    public record Page(
            int size,
            long totalElements,
            int totalPages,
            int number) {
    }
}
