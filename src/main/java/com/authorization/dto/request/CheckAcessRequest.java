package com.authorization.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckAcessRequest {

    @NotBlank(message = "accessToken cannot be blank")
    private String accessTokenUUID;
    @NotBlank(message = "service field cannot be blank")
    private String service;
    @NotBlank(message = "field resource cannot be blank")
    private String path;

}
