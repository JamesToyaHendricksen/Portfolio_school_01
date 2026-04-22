package com.example.lifeshieldai.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityEvent {
    private Long id;
    private Long userId;
    private String eventType;
    private String riskLevel;
    private String status;
    private String channel;
    private LocalDateTime detectedAt;
    private String reason;
    private String recommendation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
