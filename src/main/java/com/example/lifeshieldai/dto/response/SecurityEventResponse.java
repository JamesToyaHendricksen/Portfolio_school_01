package com.example.lifeshieldai.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SecurityEventResponse {
    Long id;
    Long userId;
    String eventType;
    String riskLevel;
    String status;
    String channel;
    LocalDateTime detectedAt;
    String reason;
    String recommendation;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
