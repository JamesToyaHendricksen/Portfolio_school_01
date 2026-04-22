package com.example.lifeshieldai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SecurityEventCreateRequest {
    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 50)
    private String eventType;

    @NotBlank
    @Size(max = 10)
    private String riskLevel;

    @NotBlank
    @Size(max = 20)
    private String status;

    @NotBlank
    @Size(max = 30)
    private String channel;

    @NotNull
    private LocalDateTime detectedAt;

    @NotBlank
    @Size(max = 500)
    private String reason;

    @Size(max = 500)
    private String recommendation;
}
