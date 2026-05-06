package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.dto.response.EmailMonitoringCheckResponse;
import com.example.lifeshieldai.security.AuthenticatedUser;
import com.example.lifeshieldai.service.EmailMonitoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email-monitoring")
@RequiredArgsConstructor
public class EmailMonitoringController {
    private final EmailMonitoringService emailMonitoringService;

    @PostMapping("/check")
    public ResponseEntity<EmailMonitoringCheckResponse> check(
            @Valid @RequestBody EmailMonitoringCheckRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(emailMonitoringService.check(request, user));
    }
}
