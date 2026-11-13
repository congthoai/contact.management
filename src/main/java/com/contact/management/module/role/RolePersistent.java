package com.contact.management.module.role;

import java.util.List;

import org.springframework.stereotype.Component;

import com.contact.management.component.jdbc.MyJdbcTemplate;
import com.contact.management.module.role.domain.Role;
import com.contact.management.utils.MapUtils;

@Component
public class RolePersistent {

    private final MyJdbcTemplate jdbc;

    public RolePersistent(final MyJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Role> findByUserId(final long userId) {
        return jdbc.query("""
                SELECT r.id, r.name
                FROM "role" AS r
                INNER JOIN app_user_role AS user_role ON user_role.role_id = r.id
                WHERE user_role.user_id = :userId
                ORDER BY r.name ASC
                """, MapUtils.of("userId", userId),
                rs -> new Role(rs.getLong("id"), rs.getString("name")));
    }
}
