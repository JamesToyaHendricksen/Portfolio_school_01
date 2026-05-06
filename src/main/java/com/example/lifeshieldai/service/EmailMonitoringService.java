package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.request.EmailMonitoringCheckRequest;
import com.example.lifeshieldai.dto.response.EmailMonitoringCheckResponse;
import com.example.lifeshieldai.security.AuthenticatedUser;

public interface EmailMonitoringService {
    EmailMonitoringCheckResponse check(EmailMonitoringCheckRequest request, AuthenticatedUser user);
}
