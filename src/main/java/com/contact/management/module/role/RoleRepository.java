package com.contact.management.module.role;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.contact.management.module.role.domain.Role;

@Repository
public class RoleRepository {

    private final RolePersistent persistent;

    public RoleRepository(final RolePersistent persistent) {
        this.persistent = persistent;
    }

    List<Role> findByUserId(final long userId) {
        return persistent.findByUserId(userId);
    }
}
