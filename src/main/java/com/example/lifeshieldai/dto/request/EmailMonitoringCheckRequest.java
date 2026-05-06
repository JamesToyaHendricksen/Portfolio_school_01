package com.example.lifeshieldai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailMonitoringCheckRequest {
    @NotBlank
    @Email
    @Size(max = 255)
    private String senderEmail;

    @NotBlank
    @Size(max = 120)
    private String subject;

    @NotBlank
    @Size(max = 2000)
    private String body;

    private boolean hasUrl;

    private boolean hasAttachment;
}
