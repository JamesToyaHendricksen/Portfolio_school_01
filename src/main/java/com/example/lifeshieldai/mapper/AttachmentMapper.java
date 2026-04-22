package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.Attachment;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AttachmentMapper {
    int insert(Attachment attachment);
    List<Attachment> findBySecurityEventId(@Param("securityEventId") Long securityEventId);
}
