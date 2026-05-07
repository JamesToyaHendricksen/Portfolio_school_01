package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.LoginRequest;
import com.example.lifeshieldai.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface SessionService {
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);
    LoginResponse current(Authentication authentication);
    void logout(HttpServletRequest httpRequest);
}
