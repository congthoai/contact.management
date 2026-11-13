package com.contact.management.module.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contact.management.module.auth.domain.AuthToken;
import com.contact.management.module.auth.domain.LoginInfo;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(final AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public AuthToken login(@Valid @RequestBody final LoginInfo info) {
        return service.login(info);
    }
}
