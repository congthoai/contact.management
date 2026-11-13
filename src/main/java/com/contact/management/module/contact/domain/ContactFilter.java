package com.contact.management.module.contact.domain;

public record ContactFilter(
        String q,
        Category category,
        boolean favorites,
        ContactSort sort) {

    public ContactFilter {
        q = q == null ? "" : q;
        sort = sort == null ? ContactSort.NAME : sort;
    }

    public boolean hasKeyword() {
        return !q.isBlank();
    }
}
