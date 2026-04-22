package com.example.lifeshieldai.service.impl;

import com.example.lifeshieldai.dto.response.AttachmentResponse;
import com.example.lifeshieldai.entity.Attachment;
import com.example.lifeshieldai.exception.BusinessException;
import com.example.lifeshieldai.mapper.AttachmentMapper;
import com.example.lifeshieldai.mapper.SecurityEventMapper;
import com.example.lifeshieldai.service.AttachmentService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private static final List<String> ALLOWED_TYPES = List.of("image/png", "image/jpeg", "application/pdf");

    private final AttachmentMapper attachmentMapper;
    private final SecurityEventMapper securityEventMapper;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public AttachmentResponse upload(Long securityEventId, MultipartFile file) {
        if (securityEventMapper.findById(securityEventId) == null) {
            throw new BusinessException("対象の危険イベントが存在しません");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("ファイルを選択してください");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException("許可されていないファイル形式です");
        }

        try {
            Path directory = Paths.get(uploadDir);
            Files.createDirectories(directory);
            String originalFileName = file.getOriginalFilename() == null ? "attachment" : file.getOriginalFilename();
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            Path filePath = directory.resolve(storedFileName);
            file.transferTo(filePath);

            Attachment attachment = Attachment.builder()
                    .securityEventId(securityEventId)
                    .fileName(originalFileName)
                    .filePath(filePath.toString())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            attachmentMapper.insert(attachment);
            return toResponse(attachment);
        } catch (IOException ex) {
            throw new BusinessException("ファイルのアップロードに失敗しました");
        }
    }

    @Override
    public List<AttachmentResponse> findBySecurityEventId(Long securityEventId) {
        return attachmentMapper.findBySecurityEventId(securityEventId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AttachmentResponse toResponse(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .securityEventId(attachment.getSecurityEventId())
                .fileName(attachment.getFileName())
                .filePath(attachment.getFilePath())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
