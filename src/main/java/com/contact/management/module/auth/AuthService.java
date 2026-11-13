package com.contact.management.module.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.contact.management.common.security.JwtTokenService;
import com.contact.management.module.auth.domain.AuthToken;
import com.contact.management.module.auth.domain.LoginInfo;
import com.contact.management.module.user.domain.UserPrincipal;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthService(final AuthenticationManager authenticationManager, final JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthToken login(final LoginInfo info) {
        final Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(info.username(), info.password()));
        final UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        return new AuthToken(jwtTokenService.generateToken(user), "Bearer");
    }
}
