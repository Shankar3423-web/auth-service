package com.authorization.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank(message = "refreshToken cannot be blank")
    private String refreshTokenUUID;

}
