package com.authorization.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RevokeTokenRequest {

    @NotBlank(message = "accessToken cannot be blank")
    private String accessTokenUUID;
}
