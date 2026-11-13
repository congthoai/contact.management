package com.contact.management.module.role.domain;

public class Role {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";

    public final long id;
    public final String name;

    public Role(final long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
