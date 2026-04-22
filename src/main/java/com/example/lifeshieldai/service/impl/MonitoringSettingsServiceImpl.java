package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.request.MonitoringSettingsUpdateRequest;
import com.example.lifeshieldai.dto.response.MonitoringSettingsResponse;
import com.example.lifeshieldai.entity.MonitoringSetting;
import com.example.lifeshieldai.exception.NotFoundException;
import com.example.lifeshieldai.mapper.MonitoringSettingMapper;
import com.example.lifeshieldai.service.MonitoringSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitoringSettingsServiceImpl implements MonitoringSettingsService {
    private final MonitoringSettingMapper monitoringSettingMapper;

    @Override
    public MonitoringSettingsResponse findByUserId(Long userId) {
        return toResponse(getExistingSetting(userId));
    }

    @Override
    public MonitoringSettingsResponse update(Long userId, MonitoringSettingsUpdateRequest request) {
        MonitoringSetting setting = getExistingSetting(userId);
        setting.setEmailMonitoring(request.getEmailMonitoring());
        setting.setSnsMonitoring(request.getSnsMonitoring());
        setting.setNetworkMonitoring(request.getNetworkMonitoring());
        setting.setNotificationEnabled(request.getNotificationEnabled());
        setting.setNotificationEmail(request.getNotificationEmail());
        monitoringSettingMapper.update(setting);
        return findByUserId(userId);
    }

    private MonitoringSetting getExistingSetting(Long userId) {
        MonitoringSetting setting = monitoringSettingMapper.findByUserId(userId);
        if (setting == null) {
            throw new NotFoundException("監視設定が見つかりません");
        }
        return setting;
    }

    private MonitoringSettingsResponse toResponse(MonitoringSetting setting) {
        return MonitoringSettingsResponse.builder()
                .id(setting.getId())
                .userId(setting.getUserId())
                .emailMonitoring(setting.getEmailMonitoring())
                .snsMonitoring(setting.getSnsMonitoring())
                .networkMonitoring(setting.getNetworkMonitoring())
                .notificationEnabled(setting.getNotificationEnabled())
                .notificationEmail(setting.getNotificationEmail())
                .build();
    }
}
