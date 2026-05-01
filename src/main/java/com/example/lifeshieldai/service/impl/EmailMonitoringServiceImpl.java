package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.dto.response.EmailMonitoringCheckResponse;
import com.example.lifeshieldai.dto.response.SecurityEventResponse;
import com.example.lifeshieldai.entity.SecurityEvent;
import com.example.lifeshieldai.mapper.SecurityEventMapper;
import com.example.lifeshieldai.security.AuthenticatedUser;
import com.example.lifeshieldai.service.EmailMonitoringService;
import com.example.lifeshieldai.service.EmailNotificationService;
import com.example.lifeshieldai.service.EmailRiskAnalyzer;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailMonitoringServiceImpl implements EmailMonitoringService {
    private final EmailRiskAnalyzer emailRiskAnalyzer;
    private final SecurityEventMapper securityEventMapper;
    private final EmailNotificationService emailNotificationService;

    @Override
    @Transactional
    public EmailMonitoringCheckResponse check(EmailMonitoringCheckRequest request, AuthenticatedUser user) {
        EmailRiskAnalyzer.AnalysisResult result = emailRiskAnalyzer.analyze(request);

        SecurityEvent event = SecurityEvent.builder()
                .userId(user.getId())
                .eventType("MAIL")
                .riskLevel(result.riskLevel())
                .status("未対応")
                .channel("メール")
                .detectedAt(LocalDateTime.now())
                .reason(result.reason())
                .recommendation(result.recommendation())
                .build();
        securityEventMapper.insert(event);

        SecurityEvent saved = securityEventMapper.findById(event.getId());
        boolean notificationCreated = false;
        if ("高".equals(result.riskLevel())) {
            emailNotificationService.notifySecurityEvent(
                    user.getId(),
                    event.getId(),
                    user.getUsername(),
                    "メール監視 高リスク通知");
            notificationCreated = true;
        }

        return EmailMonitoringCheckResponse.builder()
                .riskLevel(result.riskLevel())
                .reason(result.reason())
                .recommendation(result.recommendation())
                .matchedRules(result.matchedRules())
                .event(toResponse(saved != null ? saved : event))
                .notificationCreated(notificationCreated)
                .build();
    }

    private SecurityEventResponse toResponse(SecurityEvent event) {
        return SecurityEventResponse.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .riskLevel(event.getRiskLevel())
                .status(event.getStatus())
                .channel(event.getChannel())
                .detectedAt(event.getDetectedAt())
                .reason(event.getReason())
                .recommendation(event.getRecommendation())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
