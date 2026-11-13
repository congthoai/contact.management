package com.contact.management.module.user.validator;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.contact.management.domain.ResponseException;
import com.contact.management.module.user.domain.UserCreationInfo;

@Component
public class UserCreationInfoValidator {

    public void validate(final UserCreationInfo info) {
        if (info.password() == null
                || info.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw ResponseException.wrongParam("password");
        }
    }
}
