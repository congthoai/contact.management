package com.contact.management.module.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.contact.management.module.user.domain.UserCreationInfo;
import com.contact.management.module.user.domain.UserPrincipal;

@Repository
public class UserRepository {

    private final UserPersistent persistent;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(final UserPersistent persistent, final PasswordEncoder passwordEncoder) {
        this.persistent = persistent;
        this.passwordEncoder = passwordEncoder;
    }

    UserPrincipal findByUsername(final String username) {
        return persistent.findByUsername(username);
    }

    Long create(final UserCreationInfo info) {
        final String encodedPassword = passwordEncoder.encode(info.password());

        return persistent.create(info.username(), encodedPassword, info.firstName(), info.lastName());
    }
}
