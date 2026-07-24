package com.authorization.service;

import com.authorization.dto.AuthSessionDTO;
import com.authorization.model.TokenResponse;

public interface TokenService {

    public boolean validateToken(String accessToken);
    public TokenResponse generaterefreshToken(String refreshToken);

    public boolean revokeAccessToken(String accessToken);

    public TokenResponse authenticate(String authorizationHeader);

    public AuthSessionDTO getActiveAuthSession(String accessToken);


}
