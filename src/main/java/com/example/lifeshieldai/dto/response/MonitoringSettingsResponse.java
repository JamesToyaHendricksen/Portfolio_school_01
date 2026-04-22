package com.example.lifeshieldai.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MonitoringSettingsResponse {
    Long id;
    Long userId;
    Boolean emailMonitoring;
    Boolean snsMonitoring;
    Boolean networkMonitoring;
    Boolean notificationEnabled;
    String notificationEmail;
}
