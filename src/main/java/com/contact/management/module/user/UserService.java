package com.contact.management.module.user;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.contact.management.domain.ResponseCode;
import com.contact.management.domain.ResponseException;
import com.contact.management.module.role.RoleService;
import com.contact.management.module.role.domain.Role;
import com.contact.management.module.user.domain.UserCreationInfo;
import com.contact.management.module.user.domain.UserPrincipal;
import com.contact.management.module.user.domain.UserProfile;
import com.contact.management.module.user.validator.UserCreationInfoValidator;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleService roleService;
    private final UserCreationInfoValidator validator;

    public UserService(final UserRepository repository, final RoleService roleService,
            final UserCreationInfoValidator validator) {
        this.repository = repository;
        this.roleService = roleService;
        this.validator = validator;
    }

    public UserPrincipal findByUsername(final String username) {
        final UserPrincipal user = repository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        final List<String> roles = roleService.findByUserId(user.id)
                .stream()
                .map(role -> role.name)
                .toList();

        return new UserPrincipal(user.id, user.username, user.password,
                user.firstName, user.lastName, roles);
    }

    public UserProfile getProfile(final String username) {
        final UserPrincipal user = findByUsername(username);

        return new UserProfile(user.id, user.username, user.firstName, user.lastName, user.roles);
    }

    public Long register(final UserCreationInfo info) {
        validator.validate(info);

        final Long userId = repository.create(info);
        if (userId == null) {
            throw ResponseException.error(ResponseCode.Conflict, "Username is already in use");
        }

        return userId;
    }
}
