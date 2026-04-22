package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.MonitoringSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MonitoringSettingMapper {
    MonitoringSetting findByUserId(@Param("userId") Long userId);
    int update(MonitoringSetting setting);
}
