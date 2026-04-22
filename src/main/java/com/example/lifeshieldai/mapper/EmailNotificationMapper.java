package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.EmailNotification;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmailNotificationMapper {
    int insert(EmailNotification notification);
    int updateStatus(@Param("id") Long id, @Param("sendStatus") String sendStatus);
    List<EmailNotification> findAll();
}
