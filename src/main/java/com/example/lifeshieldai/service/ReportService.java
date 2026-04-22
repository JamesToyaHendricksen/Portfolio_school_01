package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.response.PageResponse;
import com.example.lifeshieldai.dto.response.ReportResponse;
import java.time.LocalDate;

public interface ReportService {
    PageResponse<ReportResponse> findAll(String keyword, String reportType, LocalDate startDate, LocalDate endDate,
                                         String sortBy, String sortDir, int page, int size);
    ReportResponse findById(Long id);
    byte[] exportCsv(String keyword, String reportType, LocalDate startDate, LocalDate endDate, String sortBy, String sortDir);
}
