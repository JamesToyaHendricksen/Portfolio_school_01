package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.response.AttachmentResponse;
import com.example.lifeshieldai.service.AttachmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/api/attachments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AttachmentResponse> upload(@RequestParam Long securityEventId,
            @RequestParam MultipartFile file) {
        return ResponseEntity.ok(attachmentService.upload(securityEventId, file));
    }

    @GetMapping("/api/security-events/{id}/attachments")
    public ResponseEntity<List<AttachmentResponse>> findBySecurityEventId(@PathVariable("id") Long securityEventId) {
        return ResponseEntity.ok(attachmentService.findBySecurityEventId(securityEventId));
    }
}
