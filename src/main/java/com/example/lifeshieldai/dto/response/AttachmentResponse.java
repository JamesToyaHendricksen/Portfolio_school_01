package com.example.lifeshieldai.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AttachmentResponse {
    Long id;
    Long securityEventId;
    String fileName;
    String filePath;
    String fileType;
    Long fileSize;
    LocalDateTime createdAt;
}
