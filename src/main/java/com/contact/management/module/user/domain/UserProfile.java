package com.contact.management.module.user.domain;

import java.util.List;

public class UserProfile {

    public final long id;
    public final String username;
    public final String firstName;
    public final String lastName;
    public final List<String> roles;

    public UserProfile(final long id, final String username, final String firstName, final String lastName,
            final List<String> roles) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = List.copyOf(roles);
    }

    public String fullName() {
        return (firstName + " " + lastName).trim();
    }
}
