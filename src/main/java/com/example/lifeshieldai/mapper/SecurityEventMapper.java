package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.SecurityEvent;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SecurityEventMapper {
    List<SecurityEvent> findAll(
            @Param("keyword") String keyword,
            @Param("riskLevel") String riskLevel,
            @Param("status") String status,
            @Param("channel") String channel,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("sortDir") String sortDir,
            @Param("limit") int limit,
            @Param("offset") int offset);

    long countAll(
            @Param("keyword") String keyword,
            @Param("riskLevel") String riskLevel,
            @Param("status") String status,
            @Param("channel") String channel,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    SecurityEvent findById(@Param("id") Long id);
    int insert(SecurityEvent event);
    int update(SecurityEvent event);
    int softDelete(@Param("id") Long id);
}
