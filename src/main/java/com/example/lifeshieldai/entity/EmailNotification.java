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
public class EmailNotification {
    private Long id;
    private Long userId;
    private Long securityEventId;
    private String notificationType;
    private String recipientEmail;
    private LocalDateTime sentAt;
    private String sendStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
