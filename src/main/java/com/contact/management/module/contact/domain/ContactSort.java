package com.contact.management.module.contact.domain;

public enum ContactSort {
    NAME("Name A-Z", "first_name ASC, last_name ASC, id ASC"),
    NAME_DESC("Name Z-A", "first_name DESC, last_name DESC, id DESC"),
    COMPANY("Company", "company ASC NULLS LAST, first_name ASC, last_name ASC"),
    NEWEST("Recently added", "created_at DESC, id DESC"),
    UPDATED("Recently updated", "updated_at DESC, id DESC");

    public final String label;
    public final String orderBy;

    ContactSort(final String label, final String orderBy) {
        this.label = label;
        this.orderBy = orderBy;
    }
}
