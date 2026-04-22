package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.request.MonitoringSettingsUpdateRequest;
import com.example.lifeshieldai.dto.response.MonitoringSettingsResponse;
import com.example.lifeshieldai.security.AuthenticatedUser;
import com.example.lifeshieldai.service.MonitoringSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/monitoring-settings")
@RequiredArgsConstructor
public class MonitoringSettingsController {
    private final MonitoringSettingsService monitoringSettingsService;

    @GetMapping("/me")
    public ResponseEntity<MonitoringSettingsResponse> findCurrent(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(monitoringSettingsService.findByUserId(user.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<MonitoringSettingsResponse> update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody MonitoringSettingsUpdateRequest request) {
        return ResponseEntity.ok(monitoringSettingsService.update(user.getId(), request));
    }
}
