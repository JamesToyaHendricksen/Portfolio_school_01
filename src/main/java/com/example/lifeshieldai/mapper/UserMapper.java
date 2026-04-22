package com.example.lifeshieldai.mapper;

import com.example.lifeshieldai.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);
    User findByEmail(@Param("email") String email);
    List<User> findAll();
}
