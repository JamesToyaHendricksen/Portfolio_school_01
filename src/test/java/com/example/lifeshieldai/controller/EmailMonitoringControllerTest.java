package com.example.lifeshieldai.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.lifeshieldai.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EmailMonitoringControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void check_returnsUnauthorized_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/email-monitoring/check")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void check_returnsHighRiskResult_whenSuspiciousMailIsSubmitted() throws Exception {
        mockMvc.perform(post("/api/email-monitoring/check")
                        .with(authentication(authenticatedUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("高"))
                .andExpect(jsonPath("$.notificationCreated").value(true))
                .andExpect(jsonPath("$.event.eventType").value("MAIL"));
    }

    @Test
    void check_returnsBadRequest_whenSenderEmailIsInvalid() throws Exception {
        String payload = """
                {
                  "senderEmail": "invalid-mail",
                  "subject": "確認してください",
                  "body": "本文です",
                  "hasUrl": false,
                  "hasAttachment": false
                }
                """;

        mockMvc.perform(post("/api/email-monitoring/check")
                        .with(authentication(authenticatedUser()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    private UsernamePasswordAuthenticationToken authenticatedUser() {
        AuthenticatedUser user = new AuthenticatedUser(2L, "山田 太郎", "taro.yamada@example.com", "password", "USER");
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    private String validPayload() {
        return """
                {
                  "senderEmail": "notice@example.com",
                  "subject": "至急 アカウント停止のお知らせ",
                  "body": "パスワードを確認してください。",
                  "hasUrl": true,
                  "hasAttachment": true
                }
                """;
    }
}
