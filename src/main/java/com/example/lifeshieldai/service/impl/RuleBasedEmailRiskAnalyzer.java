package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.service.EmailRiskAnalyzer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedEmailRiskAnalyzer implements EmailRiskAnalyzer {
    private static final List<String> SUSPICIOUS_WORDS = List.of(
            "至急",
            "確認してください",
            "アカウント停止",
            "送金",
            "パスワード",
            "本人確認",
            "支払い",
            "期限"
    );

    @Override
    public AnalysisResult analyze(EmailMonitoringCheckRequest request) {
        List<String> matchedRules = new ArrayList<>();
        int score = 0;

        String text = (request.getSubject() + "\n" + request.getBody()).toLowerCase(Locale.ROOT);
        for (String word : SUSPICIOUS_WORDS) {
            if (text.contains(word.toLowerCase(Locale.ROOT))) {
                score += 2;
                matchedRules.add("不審な文言: " + word);
            }
        }

        if (request.isHasUrl()) {
            score += 2;
            matchedRules.add("URLあり");
        }
        if (request.isHasAttachment()) {
            score += 1;
            matchedRules.add("添付ファイルあり");
        }

        String riskLevel = score >= 5 ? "高" : score >= 2 ? "中" : "低";
        String reason = matchedRules.isEmpty()
                ? "不審な条件には該当しませんでした。"
                : String.join("、", matchedRules) + " を検知しました。";
        String recommendation = switch (riskLevel) {
            case "高" -> "リンクや添付ファイルを開かず、送信元を別経路で確認してください。";
            case "中" -> "本文や送信元に違和感がないか確認し、必要に応じて保留してください。";
            default -> "現時点では大きな危険は見つかっていません。";
        };

        return new AnalysisResult(riskLevel, reason, recommendation, matchedRules);
    }
}
