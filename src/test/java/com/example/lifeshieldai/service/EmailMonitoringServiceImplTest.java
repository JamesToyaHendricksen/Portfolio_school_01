package com.example.lifeshieldai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.entity.SecurityEvent;
import com.example.lifeshieldai.mapper.SecurityEventMapper;
import com.example.lifeshieldai.security.AuthenticatedUser;
import com.example.lifeshieldai.service.impl.EmailMonitoringServiceImpl;
import com.example.lifeshieldai.service.impl.RuleBasedEmailRiskAnalyzer;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailMonitoringServiceImplTest {
    @Mock
    private SecurityEventMapper securityEventMapper;

    @Mock
    private EmailNotificationService emailNotificationService;

    @Test
    void check_registersHighRiskEventAndNotification_whenSuspiciousMailIsSubmitted() {
        EmailMonitoringService service = new EmailMonitoringServiceImpl(
                new RuleBasedEmailRiskAnalyzer(),
                securityEventMapper,
                emailNotificationService);
        AuthenticatedUser user = new AuthenticatedUser(2L, "山田 太郎", "taro.yamada@example.com", "password", "USER");
        EmailMonitoringCheckRequest request = highRiskRequest();

        org.mockito.Mockito.doAnswer(invocation -> {
            SecurityEvent event = invocation.getArgument(0);
            event.setId(101L);
            return 1;
        }).when(securityEventMapper).insert(any(SecurityEvent.class));
        when(securityEventMapper.findById(101L)).thenReturn(SecurityEvent.builder()
                .id(101L)
                .userId(2L)
                .eventType("MAIL")
                .riskLevel("高")
                .status("未対応")
                .channel("メール")
                .detectedAt(LocalDateTime.now())
                .reason("不審な文言: 至急、URLあり を検知しました。")
                .recommendation("リンクや添付ファイルを開かず、送信元を別経路で確認してください。")
                .build());

        var response = service.check(request, user);

        ArgumentCaptor<SecurityEvent> eventCaptor = ArgumentCaptor.forClass(SecurityEvent.class);
        verify(securityEventMapper).insert(eventCaptor.capture());
        verify(emailNotificationService).notifySecurityEvent(2L, 101L, "taro.yamada@example.com", "メール監視 高リスク通知");
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("MAIL");
        assertThat(eventCaptor.getValue().getChannel()).isEqualTo("メール");
        assertThat(response.getRiskLevel()).isEqualTo("高");
        assertThat(response.isNotificationCreated()).isTrue();
    }

    @Test
    void check_registersLowRiskEventWithoutNotification_whenMailHasNoRiskSignal() {
        EmailMonitoringService service = new EmailMonitoringServiceImpl(
                new RuleBasedEmailRiskAnalyzer(),
                securityEventMapper,
                emailNotificationService);
        AuthenticatedUser user = new AuthenticatedUser(2L, "山田 太郎", "taro.yamada@example.com", "password", "USER");
        EmailMonitoringCheckRequest request = new EmailMonitoringCheckRequest();
        request.setSenderEmail("friend@example.com");
        request.setSubject("週末の予定");
        request.setBody("次の予定を確認しましょう。");

        org.mockito.Mockito.doAnswer(invocation -> {
            SecurityEvent event = invocation.getArgument(0);
            event.setId(102L);
            return 1;
        }).when(securityEventMapper).insert(any(SecurityEvent.class));

        var response = service.check(request, user);

        verify(emailNotificationService, never()).notifySecurityEvent(any(), any(), any(), any());
        assertThat(response.getRiskLevel()).isEqualTo("低");
        assertThat(response.isNotificationCreated()).isFalse();
    }

    private EmailMonitoringCheckRequest highRiskRequest() {
        EmailMonitoringCheckRequest request = new EmailMonitoringCheckRequest();
        request.setSenderEmail("notice@example.com");
        request.setSubject("至急 アカウント停止のお知らせ");
        request.setBody("パスワードを確認してください。");
        request.setHasUrl(true);
        request.setHasAttachment(true);
        return request;
    }
}
