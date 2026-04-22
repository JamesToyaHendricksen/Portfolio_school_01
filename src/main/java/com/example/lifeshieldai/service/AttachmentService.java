package com.example.lifeshieldai.service;

import com.example.lifeshieldai.dto.response.AttachmentResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentResponse upload(Long securityEventId, MultipartFile file);
    List<AttachmentResponse> findBySecurityEventId(Long securityEventId);
}
