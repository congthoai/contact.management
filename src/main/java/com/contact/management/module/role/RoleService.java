package com.contact.management.module.role;

import java.util.List;

import org.springframework.stereotype.Service;

import com.contact.management.module.role.domain.Role;

@Service
public class RoleService {

    private final RoleRepository repository;

    public RoleService(final RoleRepository repository) {
        this.repository = repository;
    }

    public List<Role> findByUserId(final long userId) {
        return repository.findByUserId(userId);
    }
}
