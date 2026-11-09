package com.authorization.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private boolean success;
    private String code;          // ("TOKEN_VALID", "ACCESS_DENIED")
    private String message;       // Human-readable explanation
    private String tokenStatus;   // "VALID", "INVALID", "EXPIRED"  (optional)
    private String clientId;
}
