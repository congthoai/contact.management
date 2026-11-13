package com.contact.management.module.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contact.management.module.user.domain.UserCreationInfo;
import com.contact.management.module.user.domain.UserProfile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(final UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Long register(@Valid @RequestBody final UserCreationInfo info) {
        return service.register(info);
    }

    @GetMapping("/me")
    public UserProfile getProfile(@AuthenticationPrincipal final Jwt jwt) {
        return service.getProfile(jwt.getSubject());
    }
}
