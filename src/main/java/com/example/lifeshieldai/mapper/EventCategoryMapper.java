package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.EventCategory;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventCategoryMapper {
    List<EventCategory> findAll();
}
