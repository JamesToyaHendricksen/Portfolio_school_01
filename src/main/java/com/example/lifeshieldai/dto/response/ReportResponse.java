package com.example.lifeshieldai.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReportResponse {
    Long id;
    Long userId;
    String reportType;
    LocalDate targetPeriodStart;
    LocalDate targetPeriodEnd;
    String summary;
    LocalDateTime generatedAt;
}
