package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.response.EmailNotificationResponse;
import java.util.List;

public interface EmailNotificationService {
    void notifySecurityEvent(Long userId, Long securityEventId, String recipientEmail, String notificationType);
    List<EmailNotificationResponse> findAll();
}
