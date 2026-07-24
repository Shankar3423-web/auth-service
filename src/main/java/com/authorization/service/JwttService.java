package com.authorization.service;


import org.springframework.security.oauth2.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwttService {

    private final JwtEncoder jwtEncoder;

    private long expirySeconds;

    public JwttService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public Jwt generateToken(String clientId, String type, long expirySeconds) {

        Instant now = Instant.now();
        long epochSeconds = now.getEpochSecond();

        Instant expiryInstant = Instant.ofEpochSecond(epochSeconds + expirySeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(clientId)
                .issuedAt(now)
                .expiresAt(expiryInstant)
                .issuer("http://localhost:8080")
                .claim("type", type)  // "access" or "refresh"
                .build();

        JwsHeader header = JwsHeader.with(() -> "RS256").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims));

    }

    public boolean isAccessTokenValid(Jwt jwt) {
        return jwt.getExpiresAt().isAfter(Instant.now()) &&
                "access".equals(jwt.getClaim("type")) &&
                "http://localhost:8080".equals(jwt.getIssuer().toString()) &&
                jwt.getSubject() != null;
    }
}