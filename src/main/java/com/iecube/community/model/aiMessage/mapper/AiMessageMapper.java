package com.iecube.community.model.aiMessage.mapper;

import com.iecube.community.model.aiMessage.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiMessageMapper {
    int insert(AiMessage record);
}
