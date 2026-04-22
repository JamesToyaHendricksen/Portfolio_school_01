package com.example.lifeshieldai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.lifeshieldai.dto.response.LoginResponse;
import com.example.lifeshieldai.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SessionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void login_returnsCurrentUser_whenCredentialsAreValid() throws Exception {
        when(sessionService.login(any(), any())).thenReturn(LoginResponse.builder()
                .id(1L)
                .name("管理者ユーザー")
                .email("admin@lifeshield.ai")
                .role("ADMIN")
                .message("ログインしました")
                .build());

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@lifeshield.ai",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@lifeshield.ai"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.message").value("ログインしました"));
    }

    @Test
    void login_returnsBadRequest_whenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "",
                                  "password": "password"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("入力内容が不正です"));
    }

    @Test
    void login_returnsUnauthorized_whenPasswordIsInvalid() throws Exception {
        when(sessionService.login(any(), any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@lifeshield.ai",
                                  "password": "invalid-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("メールアドレスまたはパスワードが正しくありません"));
    }

    @Test
    void logout_returnsNoContent_whenAuthenticated() throws Exception {
        doNothing().when(sessionService).logout(any());

        mockMvc.perform(delete("/api/sessions/current")
                        .with(user("admin@lifeshield.ai").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
