package com.authorization.controller;

import com.authorization.dto.request.CheckAcessRequest;
import com.authorization.dto.response.AuthResponse;
import com.authorization.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/resource-access-check")
    public ResponseEntity<AuthResponse> checkAccess(@RequestBody @Valid CheckAcessRequest checkAcessRequest) {

        String clientId = resourceService.checkAccess(checkAcessRequest.getAccessTokenUUID(), checkAcessRequest.getService(), checkAcessRequest.getPath());

        if (clientId != null) {
            return ResponseEntity.ok(AuthResponse.builder()
                            .success(true)
                            .code("ACCESS_GRANTED")
                            .message("Client has access")
                            .clientId(clientId)
                            .build()
            );
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(AuthResponse.builder()
                        .success(false)
                        .code("ACCESS_DENIED")
                        .message("Client does not have required role")
                        .build()
        );
    }
}
