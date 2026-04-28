package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.response.ScanResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

@Service
public class VirusTotalService {

    @Value("${virustotal.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public VirusTotalService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://www.virustotal.com/api/v3")
                .build();
    }

    public ScanResult scanUrl(String url) {
        String encodedUrl = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(url.getBytes());
        try {
            Map response = webClient.get()
                    .uri("/urls/" + encodedUrl)
                    .header("x-apikey", apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return parseResponse(url, response);
        } catch (Exception e) {
            return submitAndScan(url);
        }
    }

    private ScanResult submitAndScan(String url) {
        try {
            webClient.post()
                    .uri("/urls")
                    .header("x-apikey", apiKey)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("url=" + url)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            Thread.sleep(3000);
            String encodedUrl = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(url.getBytes());
            Map response = webClient.get()
                    .uri("/urls/" + encodedUrl)
                    .header("x-apikey", apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return parseResponse(url, response);
        } catch (Exception e) {
            return new ScanResult(url, false, 0, 0, 0,
                    "スキャンできませんでした: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ScanResult parseResponse(String url, Map response) {
        try {
            Map data       = (Map) response.get("data");
            Map attributes = (Map) data.get("attributes");
            Map stats      = (Map) attributes.get("last_analysis_stats");
            int malicious  = toInt(stats.get("malicious"));
            int harmless   = toInt(stats.get("harmless"));
            int undetected = toInt(stats.get("undetected"));
            boolean isMalicious = malicious > 0;
            String summary = isMalicious
                    ? "⚠️ 危険：" + malicious + "件のエンジンが脅威を検出しました"
                    : "✅ 安全：脅威は検出されませんでした";
            return new ScanResult(url, isMalicious,
                    malicious, harmless, undetected, summary);
        } catch (Exception e) {
            return new ScanResult(url, false, 0, 0, 0, "結果の解析に失敗しました");
        }
    }

    private int toInt(Object value) {
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number)  return ((Number) value).intValue();
        return 0;
    }
}
