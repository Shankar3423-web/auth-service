package com.authorization.mapper;

import com.authorization.entity.AuthSession;
import com.authorization.dto.AuthSessionDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthSessionMapper {

    public AuthSession toEntity(AuthSessionDTO dto) {
        return AuthSession.builder()
                .clientId(dto.getClientId())
                .accessTokenUUID(dto.getAccessTokenUUID())
                .accessToken(dto.getAccessToken())
                .accessTokenExpiry(dto.getAccessTokenExpiry())
                .refreshTokenUUID(dto.getRefreshTokenUUID())
                .refreshToken(dto.getRefreshToken())
                .refreshTokenExpiry(dto.getRefreshTokenExpiry())
                .isActive(dto.isActive())
                .build();
    }

    public AuthSessionDTO toAuthSessionDTO(AuthSession authSession){
        return AuthSessionDTO.builder()
                .clientId(authSession.getClientId())
                .accessTokenUUID(authSession.getAccessTokenUUID())
                .accessToken(authSession.getAccessToken())
                .accessTokenExpiry(authSession.getAccessTokenExpiry())
                .refreshTokenUUID(authSession.getRefreshTokenUUID())
                .refreshToken(authSession.getRefreshToken())
                .refreshTokenExpiry(authSession.getRefreshTokenExpiry())
                .isActive(authSession.isActive())
                .build();
    }
}