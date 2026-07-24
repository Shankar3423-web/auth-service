package com.authorization.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateTokenRequest {

    @NotBlank(message = "accessToken cannot be Blank")
    private String accessTokenUUID;

}
