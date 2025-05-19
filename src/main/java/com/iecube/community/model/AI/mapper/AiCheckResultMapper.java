package com.iecube.community.model.AI.mapper;

import com.iecube.community.model.AI.dto.AiCheckResult;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiCheckResultMapper {
    int insert(AiCheckResult record);
}
