package com.iecube.community.model.auth.mapper;

import com.iecube.community.model.auth.entity.Authority;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthMapper {
    List<String> userAuthList(String teacherId);

    List<Authority> allAuthList();
}
