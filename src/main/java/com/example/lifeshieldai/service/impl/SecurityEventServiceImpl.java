package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.request.SecurityEventCreateRequest;
import com.example.lifeshieldai.dto.request.SecurityEventUpdateRequest;
import com.example.lifeshieldai.dto.response.PageResponse;
import com.example.lifeshieldai.dto.response.SecurityEventResponse;
import com.example.lifeshieldai.entity.SecurityEvent;
import com.example.lifeshieldai.entity.User;
import com.example.lifeshieldai.exception.BusinessException;
import com.example.lifeshieldai.exception.NotFoundException;
import com.example.lifeshieldai.mapper.SecurityEventMapper;
import com.example.lifeshieldai.mapper.UserMapper;
import com.example.lifeshieldai.service.EmailNotificationService;
import com.example.lifeshieldai.service.SecurityEventService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityEventServiceImpl implements SecurityEventService {
    private final SecurityEventMapper securityEventMapper;
    private final UserMapper userMapper;
    private final EmailNotificationService emailNotificationService;

    @Override
    public PageResponse<SecurityEventResponse> findAll(String keyword, String riskLevel, String status, String channel,
                                                       LocalDateTime startDate, LocalDateTime endDate,
                                                       String sortBy, String sortDir, int page, int size) {
        validateDateRange(startDate, endDate);
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;
        List<SecurityEventResponse> content = securityEventMapper.findAll(
                        keyword, riskLevel, status, channel, startDate, endDate,
                        normalizeSecurityEventSort(sortBy), normalizeSortDir(sortDir), safeSize, offset)
                .stream()
                .map(this::toResponse)
                .toList();
        long total = securityEventMapper.countAll(keyword, riskLevel, status, channel, startDate, endDate);
        return PageResponse.<SecurityEventResponse>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(total)
                .build();
    }

    @Override
    public SecurityEventResponse findById(Long id) {
        return toResponse(getExistingEvent(id));
    }

    @Override
    public SecurityEventResponse create(SecurityEventCreateRequest request) {
        User user = userMapper.findById(request.getUserId());
        if (user == null) {
            throw new BusinessException("対象ユーザーが見つかりません");
        }

        SecurityEvent event = SecurityEvent.builder()
                .userId(request.getUserId())
                .eventType(request.getEventType())
                .riskLevel(request.getRiskLevel())
                .status(request.getStatus())
                .channel(request.getChannel())
                .detectedAt(request.getDetectedAt())
                .reason(request.getReason())
                .recommendation(request.getRecommendation())
                .build();
        securityEventMapper.insert(event);

        if (hasNotificationTarget(user)) {
            emailNotificationService.notifySecurityEvent(user.getId(), event.getId(), user.getEmail(), "危険イベント通知");
        }
        return findById(event.getId());
    }

    @Override
    public SecurityEventResponse update(Long id, SecurityEventUpdateRequest request) {
        SecurityEvent existing = getExistingEvent(id);
        if (userMapper.findById(request.getUserId()) == null) {
            throw new BusinessException("対象ユーザーが見つかりません");
        }

        existing.setUserId(request.getUserId());
        existing.setEventType(request.getEventType());
        existing.setRiskLevel(request.getRiskLevel());
        existing.setStatus(request.getStatus());
        existing.setChannel(request.getChannel());
        existing.setDetectedAt(request.getDetectedAt());
        existing.setReason(request.getReason());
        existing.setRecommendation(request.getRecommendation());
        securityEventMapper.update(existing);
        return findById(id);
    }

    @Override
    public void delete(Long id) {
        if (securityEventMapper.softDelete(id) == 0) {
            throw new NotFoundException("危険イベントが見つかりません");
        }
    }

    @Override
    public byte[] exportCsv(String keyword, String riskLevel, String status, String channel,
                            LocalDateTime startDate, LocalDateTime endDate, String sortBy, String sortDir) {
        List<SecurityEventResponse> rows = findAll(keyword, riskLevel, status, channel, startDate, endDate, sortBy, sortDir, 0, 1000)
                .getContent();
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("id,eventType,riskLevel,status,channel,detectedAt,reason,recommendation\n");
        for (SecurityEventResponse row : rows) {
            csv.append(row.getId()).append(',')
                    .append(escapeCsv(row.getEventType())).append(',')
                    .append(escapeCsv(row.getRiskLevel())).append(',')
                    .append(escapeCsv(row.getStatus())).append(',')
                    .append(escapeCsv(row.getChannel())).append(',')
                    .append(row.getDetectedAt()).append(',')
                    .append(escapeCsv(row.getReason())).append(',')
                    .append(escapeCsv(row.getRecommendation()))
                    .append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private SecurityEvent getExistingEvent(Long id) {
        SecurityEvent event = securityEventMapper.findById(id);
        if (event == null) {
            throw new NotFoundException("危険イベントが見つかりません");
        }
        return event;
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

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("終了日は開始日以降を指定してください");
        }
    }

    private String normalizeSecurityEventSort(String sortBy) {
        if (sortBy == null) {
            return "detected_at";
        }
        return switch (sortBy) {
            case "riskLevel" -> "risk_level";
            case "status" -> "status";
            case "channel" -> "channel";
            default -> "detected_at";
        };
    }

    private String normalizeSortDir(String sortDir) {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private boolean hasNotificationTarget(User user) {
        return user.getEmail() != null && !user.getEmail().isBlank();
    }
}
