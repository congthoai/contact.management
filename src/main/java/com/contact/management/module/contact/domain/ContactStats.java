package com.contact.management.module.contact.domain;

import java.util.Map;

public record ContactStats(
        long total,
        long favorites,
        Map<Category, Long> byCategory) {

    public ContactStats {
        byCategory = Map.copyOf(byCategory);
    }

    public long countFor(final Category category) {
        return byCategory.getOrDefault(category, 0L);
    }
}
