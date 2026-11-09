package com.authorization.controller;

import com.authorization.dto.request.ClientRegisterRequest;
import com.authorization.dto.response.ClientRegisterResponse;
import com.authorization.dto.response.AuthResponse;
import com.authorization.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class ClientRegistrationController {

    @Autowired
    private ClientService clientService;

    /**
     * Create or Update client credentials and plan roles in MySQL (Create / Update)
     */
    @PostMapping("/client/register")
    public ResponseEntity<ClientRegisterResponse> registerOrUpdateClient(
            @RequestBody @Valid ClientRegisterRequest request) {
        ClientRegisterResponse response = clientService.registerOrUpdateClient(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get client credentials and role details by clientId (Read)
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ClientRegisterResponse> getClientDetails(@PathVariable String clientId) {
        try {
            ClientRegisterResponse response = clientService.getClientDetails(clientId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ClientRegisterResponse.builder().success(false).build());
        }
    }

    /**
     * Deactivate client role mappings in MySQL (Delete / Suspend)
     */
    @DeleteMapping("/client/{clientId}")
    public ResponseEntity<AuthResponse> suspendClientAccess(@PathVariable String clientId) {
        boolean deactivated = clientService.deleteClientRoles(clientId);
        if (deactivated) {
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .code("CLIENT_SUSPENDED")
                    .message("Client role assignments successfully deactivated")
                    .build());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(AuthResponse.builder()
                        .success(false)
                        .code("CLIENT_NOT_FOUND")
                        .message("Client record not found")
                        .build());
    }
}
