package com.contact.management.module.contact.domain;

import java.util.List;

public record ContactSearchResult(
        List<Contact> contacts,
        long totalElements) {

    public ContactSearchResult {
        contacts = List.copyOf(contacts);
    }
}
