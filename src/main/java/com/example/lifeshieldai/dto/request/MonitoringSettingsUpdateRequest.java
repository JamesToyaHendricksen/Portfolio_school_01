package com.example.lifeshieldai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MonitoringSettingsUpdateRequest {
    @NotNull
    private Boolean emailMonitoring;

    @NotNull
    private Boolean snsMonitoring;

    @NotNull
    private Boolean networkMonitoring;

    @NotNull
    private Boolean notificationEnabled;

    @Email
    @Size(max = 255)
    private String notificationEmail;
}
