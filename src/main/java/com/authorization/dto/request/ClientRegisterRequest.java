package com.authorization.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterRequest {

    @NotBlank(message = "uid is required")
    private String uid;

    @NotBlank(message = "plan is required")
    private String plan; // "job_posting", "applicant_viewing", or "combined"

    @NotBlank(message = "clientName is required")
    private String clientName;
}
