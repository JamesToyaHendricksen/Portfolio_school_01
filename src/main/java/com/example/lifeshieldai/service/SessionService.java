package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.LoginRequest;
import com.example.lifeshieldai.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface SessionService {
    LoginResponse login(LoginRequest request, HttpServletRequest httpRequest);
    void logout(HttpServletRequest httpRequest);
}
