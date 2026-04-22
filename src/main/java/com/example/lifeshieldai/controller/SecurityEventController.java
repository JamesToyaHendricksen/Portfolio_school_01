package com.example.lifeshieldai.controller;

import com.example.lifeshieldai.dto.request.SecurityEventCreateRequest;
import com.example.lifeshieldai.dto.request.SecurityEventUpdateRequest;
import com.example.lifeshieldai.dto.response.PageResponse;
import com.example.lifeshieldai.dto.response.SecurityEventResponse;
import com.example.lifeshieldai.service.SecurityEventService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security-events")
@RequiredArgsConstructor
public class SecurityEventController {
    private final SecurityEventService securityEventService;

    @GetMapping
    public ResponseEntity<PageResponse<SecurityEventResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "detectedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(securityEventService.findAll(keyword, riskLevel, status, channel, startDate, endDate, sortBy, sortDir, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityEventResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(securityEventService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecurityEventResponse> create(@Valid @RequestBody SecurityEventCreateRequest request) {
        return ResponseEntity.ok(securityEventService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecurityEventResponse> update(@PathVariable Long id, @Valid @RequestBody SecurityEventUpdateRequest request) {
        return ResponseEntity.ok(securityEventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        securityEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "detectedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        byte[] csv = securityEventService.exportCsv(keyword, riskLevel, status, channel, startDate, endDate, sortBy, sortDir);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("security-events.csv").build());
        return ResponseEntity.ok().headers(headers).body(csv);
    }
}
