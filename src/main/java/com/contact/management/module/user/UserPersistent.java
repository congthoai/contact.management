package com.contact.management.module.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.contact.management.component.jdbc.MyJdbcTemplate;
import com.contact.management.module.user.domain.UserPrincipal;
import com.contact.management.utils.MapUtils;

@Component
public class UserPersistent {

    private final MyJdbcTemplate jdbc;

    public UserPersistent(final MyJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public UserPrincipal findByUsername(final String username) {
        return jdbc.queryForObject("""
                SELECT id, username, password, first_name, last_name
                FROM "user"
                WHERE username = :username
                """, MapUtils.of("username", username),
                rs -> new UserPrincipal(rs.getLong("id"), rs.getString("username"), rs.getString("password"),
                        rs.getString("first_name"), rs.getString("last_name"), List.of()));
    }

    Long create(final String username, final String encodedPassword,
            final String firstName, final String lastName) {
        return jdbc.updateForObject("""
                INSERT INTO "user" (username, password, first_name, last_name)
                VALUES (:username, :password, :firstName, :lastName)
                ON CONFLICT (username) DO NOTHING
                RETURNING id
                """, MapUtils.of(
                "username", username,
                "password", encodedPassword,
                "firstName", firstName,
                "lastName", lastName), Long.class);
    }
}
