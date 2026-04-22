package com.example.lifeshieldai.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringSetting {
    private Long id;
    private Long userId;
    private Boolean emailMonitoring;
    private Boolean snsMonitoring;
    private Boolean networkMonitoring;
    private Boolean notificationEnabled;
    private String notificationEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
