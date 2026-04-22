package com.example.lifeshieldai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.lifeshieldai.dto.request.SecurityEventCreateRequest;
import com.example.lifeshieldai.entity.SecurityEvent;
import com.example.lifeshieldai.entity.User;
import com.example.lifeshieldai.exception.BusinessException;
import com.example.lifeshieldai.exception.NotFoundException;
import com.example.lifeshieldai.mapper.SecurityEventMapper;
import com.example.lifeshieldai.mapper.UserMapper;
import com.example.lifeshieldai.service.impl.SecurityEventServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecurityEventServiceImplTest {

    @Mock
    private SecurityEventMapper securityEventMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private SecurityEventServiceImpl securityEventService;

    @Test
    void create_registersEventAndNotification_whenRequestIsValid() {
        SecurityEventCreateRequest request = new SecurityEventCreateRequest();
        request.setUserId(2L);
        request.setEventType("PHISHING");
        request.setRiskLevel("高");
        request.setStatus("未対応");
        request.setChannel("メール");
        request.setDetectedAt(LocalDateTime.of(2026, 4, 22, 10, 0));
        request.setReason("不審なリンクを検知");
        request.setRecommendation("リンクを開かない");

        User user = User.builder()
                .id(2L)
                .name("山田 太郎")
                .email("taro.yamada@example.com")
                .role("USER")
                .build();

        SecurityEvent inserted = SecurityEvent.builder()
                .id(10L)
                .userId(2L)
                .eventType("PHISHING")
                .riskLevel("高")
                .status("未対応")
                .channel("メール")
                .detectedAt(request.getDetectedAt())
                .reason(request.getReason())
                .recommendation(request.getRecommendation())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userMapper.findById(2L)).thenReturn(user);
        when(securityEventMapper.findById(10L)).thenReturn(inserted);

        ArgumentCaptor<SecurityEvent> captor = ArgumentCaptor.forClass(SecurityEvent.class);
        org.mockito.Mockito.doAnswer(invocation -> {
            SecurityEvent event = invocation.getArgument(0);
            event.setId(10L);
            return 1;
        }).when(securityEventMapper).insert(any(SecurityEvent.class));

        var response = securityEventService.create(request);

        verify(securityEventMapper).insert(captor.capture());
        verify(emailNotificationService).notifySecurityEvent(2L, 10L, "taro.yamada@example.com", "危険イベント通知");
        assertThat(captor.getValue().getEventType()).isEqualTo("PHISHING");
        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    void create_throwsBusinessException_whenUserDoesNotExist() {
        SecurityEventCreateRequest request = new SecurityEventCreateRequest();
        request.setUserId(999L);
        request.setEventType("PHISHING");
        request.setRiskLevel("高");
        request.setStatus("未対応");
        request.setChannel("メール");
        request.setDetectedAt(LocalDateTime.now());
        request.setReason("不審なリンクを検知");

        when(userMapper.findById(999L)).thenReturn(null);

        assertThatThrownBy(() -> securityEventService.create(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("対象ユーザーが見つかりません");

        verify(securityEventMapper, never()).insert(any());
    }

    @Test
    void findAll_throwsBusinessException_whenDateRangeIsInvalid() {
        LocalDateTime start = LocalDateTime.of(2026, 4, 22, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 4, 21, 12, 0);

        assertThatThrownBy(() -> securityEventService.findAll(null, null, null, null, start, end, "detectedAt", "desc", 0, 10))
                .isInstanceOf(BusinessException.class)
                .hasMessage("終了日は開始日以降を指定してください");
    }

    @Test
    void delete_throwsNotFoundException_whenTargetDoesNotExist() {
        when(securityEventMapper.softDelete(999L)).thenReturn(0);

        assertThatThrownBy(() -> securityEventService.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("危険イベントが見つかりません");
    }

    @Test
    void exportCsv_returnsUtf8BomAndHeader_whenNoData() {
        when(securityEventMapper.findAll(null, null, null, null, null, null, "detected_at", "desc", 1000, 0))
                .thenReturn(List.of());
        when(securityEventMapper.countAll(null, null, null, null, null, null)).thenReturn(0L);

        byte[] csv = securityEventService.exportCsv(null, null, null, null, null, null, "detectedAt", "desc");
        String csvText = new String(csv, java.nio.charset.StandardCharsets.UTF_8);

        assertThat(csvText).startsWith("\uFEFFid,eventType");
    }
}
