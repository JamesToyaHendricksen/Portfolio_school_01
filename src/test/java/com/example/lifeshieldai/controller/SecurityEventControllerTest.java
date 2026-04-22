package com.example.lifeshieldai.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_returnsUnauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/security-events"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAll_returnsPagedData_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/security-events")
                        .with(user("admin@lifeshield.ai").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    void create_returnsForbidden_forGeneralUser() throws Exception {
        mockMvc.perform(post("/api/security-events")
                        .with(user("taro.yamada@example.com").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("権限がありません"));
    }

    @Test
    void create_returnsBadRequest_whenReasonExceedsMaxLength() throws Exception {
        String tooLongReason = "a".repeat(501);
        String payload = """
                {
                  "userId": 2,
                  "eventType": "PHISHING",
                  "riskLevel": "高",
                  "status": "未対応",
                  "channel": "メール",
                  "detectedAt": "2026-04-22T10:00:00",
                  "reason": "%s",
                  "recommendation": "リンクを開かない"
                }
                """.formatted(tooLongReason);

        mockMvc.perform(post("/api/security-events")
                        .with(user("admin@lifeshield.ai").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("入力内容が不正です"));
    }

    @Test
    void create_returnsCreatedEvent_forAdmin() throws Exception {
        mockMvc.perform(post("/api/security-events")
                        .with(user("admin@lifeshield.ai").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreateRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType").value("PHISHING"))
                .andExpect(jsonPath("$.userId").value(2));
    }

    private String validCreateRequest() {
        return """
                {
                  "userId": 2,
                  "eventType": "PHISHING",
                  "riskLevel": "高",
                  "status": "未対応",
                  "channel": "メール",
                  "detectedAt": "2026-04-22T10:00:00",
                  "reason": "不審なリンクを検知",
                  "recommendation": "リンクを開かない"
                }
                """;
    }
}
