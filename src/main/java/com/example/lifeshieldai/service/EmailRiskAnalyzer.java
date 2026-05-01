package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import java.util.List;

public interface EmailRiskAnalyzer {
    AnalysisResult analyze(EmailMonitoringCheckRequest request);

    record AnalysisResult(
            String riskLevel,
            String reason,
            String recommendation,
            List<String> matchedRules
    ) {
    }
}
