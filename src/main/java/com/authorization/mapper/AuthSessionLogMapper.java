package com.authorization.mapper;

import com.authorization.entity.AuthSessionLog;
import com.authorization.dto.AuthSessionDTO;
import org.springframework.stereotype.Component;


@Component
public class AuthSessionLogMapper {

    public AuthSessionLog toEntity(AuthSessionDTO dto) {

        return AuthSessionLog.builder()
                .clientId(dto.getClientId())
                .accessTokenUUID(dto.getAccessTokenUUID())
                .accessToken(dto.getAccessToken())
                .accessTokenExpiry(dto.getAccessTokenExpiry())
                .refreshTokenUUID(dto.getRefreshTokenUUID())
                .refreshToken(dto.getRefreshToken())
                .refreshTokenExpiry(dto.getRefreshTokenExpiry())
                .build();
    }

}
