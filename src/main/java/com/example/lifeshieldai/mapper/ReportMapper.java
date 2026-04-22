package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.Report;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReportMapper {
    List<Report> findAll(
            @Param("keyword") String keyword,
            @Param("reportType") String reportType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countAll(
            @Param("keyword") String keyword,
            @Param("reportType") String reportType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Report findById(@Param("id") Long id);
}
