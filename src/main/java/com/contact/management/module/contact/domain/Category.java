package com.contact.management.module.contact.domain;

public enum Category {
    FAMILY("Family"),
    FRIEND("Friend"),
    WORK("Work"),
    CLIENT("Client"),
    OTHER("Other");

    public final String label;

    Category(final String label) {
        this.label = label;
    }
}
