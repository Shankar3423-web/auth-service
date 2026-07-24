package com.authorization.controller;

import com.authorization.dto.request.RefreshTokenRequest;
import com.authorization.dto.request.RevokeTokenRequest;
import com.authorization.dto.request.ValidateTokenRequest;
import com.authorization.dto.response.AuthResponse;
import com.authorization.model.TokenResponse;
import com.authorization.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @PostMapping("/authenticate")
    public TokenResponse authenticate(@RequestHeader("Authorization") String authorizationHeader) {
        return tokenService.authenticate(authorizationHeader);
    }

    @PostMapping("/token")
    // rename method
    public TokenResponse generateAccessTokenFromRefreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return tokenService.generaterefreshToken(refreshTokenRequest.getRefreshTokenUUID());
    }

    @PostMapping("/validate-token")
    public ResponseEntity<AuthResponse> validateToken(@RequestBody @Valid ValidateTokenRequest validateTokenRequest){
        boolean isValid = tokenService.validateToken(validateTokenRequest.getAccessTokenUUID());

        if (isValid) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .message("VALID_TOKEN")
                    .build()
            );
        }

        return ResponseEntity.ok(AuthResponse.builder()
                .success(false)
                .message("INVALID_TOKEN")
                .build()
        );
    }


    @PostMapping("/revokeAccessToken")
    public ResponseEntity<AuthResponse> revokeAccessToken(@RequestBody @Valid RevokeTokenRequest revokeTokenRequest) {
        boolean revoked = tokenService.revokeAccessToken(revokeTokenRequest.getAccessTokenUUID());

        if (revoked) {
            return ResponseEntity.ok(AuthResponse.builder()
                            .success(true)
                            .code("TOKEN_REVOKED")
                            .message("Access token has been successfully revoked")
                            .build()
            );
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthResponse.builder()
                        .success(false)
                        .code("TOKEN_REVOKE_FAILED")
                        .message("Failed to revoke the access token")
                        .build()
        );
    }


}
