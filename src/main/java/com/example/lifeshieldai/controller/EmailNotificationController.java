package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.response.EmailNotificationResponse;
import com.example.lifeshieldai.service.EmailNotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email-notifications")
@RequiredArgsConstructor
public class EmailNotificationController {
    private final EmailNotificationService emailNotificationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmailNotificationResponse>> findAll() {
        return ResponseEntity.ok(emailNotificationService.findAll());
    }
}
