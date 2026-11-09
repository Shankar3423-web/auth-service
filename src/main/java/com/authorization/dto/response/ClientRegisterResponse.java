package com.authorization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterResponse {
    private boolean success;
    private String clientId;
    private String clientSecret;
    private String clientName;
    private List<String> roles;
}
