package com.example.lifeshieldai.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailNotificationResponse {
    Long id;
    Long userId;
    Long securityEventId;
    String notificationType;
    String recipientEmail;
    LocalDateTime sentAt;
    String sendStatus;
}
