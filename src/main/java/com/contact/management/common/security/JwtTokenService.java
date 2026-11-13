package com.contact.management.common.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.contact.management.module.user.domain.UserPrincipal;

@Component
public class JwtTokenService {

    public static final String JwtIssuer = "contact-management";

    private final JwtEncoder jwtEncoder;
    private final long expirationMinutes;

    public JwtTokenService(final JwtEncoder jwtEncoder,
            @Value("${security.jwt.expiration-minutes:30}") final long expirationMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(final UserPrincipal user) {
        final Instant now = Instant.now();
        final JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(JwtIssuer)
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(user.username)
                .claim("user_id", user.id)
                .claim("first_name", user.firstName)
                .claim("last_name", user.lastName)
                .claim("roles", user.roles)
                .build();

        final JwsHeader header = JwsHeader.with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }
}
