package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.SecurityEventCreateRequest;
import com.example.lifeshieldai.dto.request.SecurityEventUpdateRequest;
import com.example.lifeshieldai.dto.response.PageResponse;
import com.example.lifeshieldai.dto.response.SecurityEventResponse;
import java.time.LocalDateTime;

public interface SecurityEventService {
    PageResponse<SecurityEventResponse> findAll(String keyword, String riskLevel, String status, String channel,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                String sortBy, String sortDir, int page, int size);
    SecurityEventResponse findById(Long id);
    SecurityEventResponse create(SecurityEventCreateRequest request);
    SecurityEventResponse update(Long id, SecurityEventUpdateRequest request);
    void delete(Long id);
    byte[] exportCsv(String keyword, String riskLevel, String status, String channel,
                     LocalDateTime startDate, LocalDateTime endDate, String sortBy, String sortDir);
}
