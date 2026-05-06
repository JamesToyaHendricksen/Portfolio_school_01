package com.example.lifeshieldai.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmailMonitoringCheckResponse {
    String riskLevel;
    String reason;
    String recommendation;
    List<String> matchedRules;
    SecurityEventResponse event;
    boolean notificationCreated;
}
