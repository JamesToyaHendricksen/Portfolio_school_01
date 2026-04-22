package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.request.LoginRequest;
import com.example.lifeshieldai.dto.response.LoginResponse;
import com.example.lifeshieldai.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ResponseEntity.ok(sessionService.login(request, httpRequest));
    }

    @DeleteMapping("/current")
    public ResponseEntity<Void> logout(HttpServletRequest httpRequest) {
        sessionService.logout(httpRequest);
        return ResponseEntity.noContent().build();
    }
}
