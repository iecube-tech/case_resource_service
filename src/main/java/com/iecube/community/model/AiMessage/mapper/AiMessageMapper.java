package com.iecube.community.model.AiMessage.mapper;

import com.iecube.community.model.AiMessage.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiMessageMapper {
    int insert(AiMessage record);
}
