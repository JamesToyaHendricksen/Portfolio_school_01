package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.MonitoringSettingsUpdateRequest;
import com.example.lifeshieldai.dto.response.MonitoringSettingsResponse;

public interface MonitoringSettingsService {
    MonitoringSettingsResponse findByUserId(Long userId);
    MonitoringSettingsResponse update(Long userId, MonitoringSettingsUpdateRequest request);
}
