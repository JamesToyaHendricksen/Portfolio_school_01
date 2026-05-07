package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.service.EmailRiskAnalyzer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedEmailRiskAnalyzer implements EmailRiskAnalyzer {
    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "至急",
            "確認してください",
            "アカウント停止",
            "支払い",
            "パスワード",
            "本人確認",
            "今すぐ",
            "期限"
    );

    @Override
    public AnalysisResult analyze(EmailMonitoringCheckRequest request) {
        Set<String> suspiciousItems = new LinkedHashSet<>();
        int score = 0;

        String text = (request.getSubject() + "\n" + request.getBody()).toLowerCase(Locale.ROOT);
        for (String keyword : SUSPICIOUS_KEYWORDS) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                score += 2;
                suspiciousItems.add("文面キーワード「" + keyword + "」");
            }
        }

        if (request.isHasUrl()) {
            score += 2;
            suspiciousItems.add("本文にURLが含まれている");
        }
        if (request.isHasAttachment()) {
            score += 1;
            suspiciousItems.add("添付ファイルが含まれている");
        }

        String riskLevel = score >= 5 ? "高" : score >= 2 ? "中" : "低";
        List<String> matchedRules = new ArrayList<>(suspiciousItems);

        String reason = matchedRules.isEmpty()
                ? "疑うべき項目は検出されませんでした。"
                : "疑うべき項目: " + String.join("、", matchedRules) + "。";

        String recommendation = switch (riskLevel) {
            case "高" -> "リンクや添付ファイルは開かず、送信元の正当性を別経路で確認してください。";
            case "中" -> "本文と送信元を再確認し、少しでも不審なら操作を中止してください。";
            default -> "現時点では重大なリスクは見つかっていません。";
        };

        return new AnalysisResult(riskLevel, reason, recommendation, matchedRules);
    }
}
