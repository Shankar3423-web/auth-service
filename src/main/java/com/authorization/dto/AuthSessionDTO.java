package com.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthSessionDTO implements Serializable {
    private String clientId;
    private String accessTokenUUID;
    private String accessToken;
    private long accessTokenExpiry;
    private String refreshTokenUUID;
    private String refreshToken;
    private long refreshTokenExpiry;
    private boolean isActive;
}