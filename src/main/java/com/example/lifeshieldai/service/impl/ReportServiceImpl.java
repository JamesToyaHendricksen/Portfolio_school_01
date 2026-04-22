package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.response.PageResponse;
import com.example.lifeshieldai.dto.response.ReportResponse;
import com.example.lifeshieldai.entity.Report;
import com.example.lifeshieldai.exception.BusinessException;
import com.example.lifeshieldai.exception.NotFoundException;
import com.example.lifeshieldai.mapper.ReportMapper;
import com.example.lifeshieldai.service.ReportService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;

    @Override
    public PageResponse<ReportResponse> findAll(String keyword, String reportType, LocalDate startDate, LocalDate endDate,
                                                String sortBy, String sortDir, int page, int size) {
        validateDateRange(startDate, endDate);
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;
        List<ReportResponse> content = reportMapper.findAll(
                        keyword, reportType, startDate, endDate, normalizeSort(sortBy), normalizeSortDir(sortDir), safeSize, offset)
                .stream()
                .map(this::toResponse)
                .toList();
        long total = reportMapper.countAll(keyword, reportType, startDate, endDate);
        return PageResponse.<ReportResponse>builder()
                .content(content)
                .page(safePage)
                .size(safeSize)
                .totalElements(total)
                .build();
    }

    @Override
    public ReportResponse findById(Long id) {
        Report report = reportMapper.findById(id);
        if (report == null) {
            throw new NotFoundException("レポートが見つかりません");
        }
        return toResponse(report);
    }

    @Override
    public byte[] exportCsv(String keyword, String reportType, LocalDate startDate, LocalDate endDate, String sortBy, String sortDir) {
        List<ReportResponse> rows = findAll(keyword, reportType, startDate, endDate, sortBy, sortDir, 0, 1000).getContent();
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("id,reportType,targetPeriodStart,targetPeriodEnd,generatedAt,summary\n");
        for (ReportResponse row : rows) {
            csv.append(row.getId()).append(',')
                    .append(escapeCsv(row.getReportType())).append(',')
                    .append(row.getTargetPeriodStart()).append(',')
                    .append(row.getTargetPeriodEnd()).append(',')
                    .append(row.getGeneratedAt()).append(',')
                    .append(escapeCsv(row.getSummary()))
                    .append('\n');
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .reportType(report.getReportType())
                .targetPeriodStart(report.getTargetPeriodStart())
                .targetPeriodEnd(report.getTargetPeriodEnd())
                .summary(report.getSummary())
                .generatedAt(report.getGeneratedAt())
                .build();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessException("終了日は開始日以降を指定してください");
        }
    }

    private String normalizeSort(String sortBy) {
        return "reportType".equals(sortBy) ? "report_type" : "generated_at";
    }

    private String normalizeSortDir(String sortDir) {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
