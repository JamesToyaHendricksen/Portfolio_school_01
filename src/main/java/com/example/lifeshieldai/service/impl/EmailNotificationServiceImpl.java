package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.response.EmailNotificationResponse;
import com.example.lifeshieldai.entity.EmailNotification;
import com.example.lifeshieldai.mapper.EmailNotificationMapper;
import com.example.lifeshieldai.service.EmailNotificationService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final EmailNotificationMapper emailNotificationMapper;

    @Override
    public void notifySecurityEvent(Long userId, Long securityEventId, String recipientEmail, String notificationType) {
        EmailNotification notification = EmailNotification.builder()
                .userId(userId)
                .securityEventId(securityEventId)
                .notificationType(notificationType)
                .recipientEmail(recipientEmail)
                .sentAt(LocalDateTime.now())
                .sendStatus("SENT")
                .build();
        emailNotificationMapper.insert(notification);
    }

    @Override
    public List<EmailNotificationResponse> findAll() {
        return emailNotificationMapper.findAll().stream()
                .map(item -> EmailNotificationResponse.builder()
                        .id(item.getId())
                        .userId(item.getUserId())
                        .securityEventId(item.getSecurityEventId())
                        .notificationType(item.getNotificationType())
                        .recipientEmail(item.getRecipientEmail())
                        .sentAt(item.getSentAt())
                        .sendStatus(item.getSendStatus())
                        .build())
                .toList();
    }
}
