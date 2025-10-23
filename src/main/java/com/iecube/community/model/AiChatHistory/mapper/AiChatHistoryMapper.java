package com.iecube.community.model.AiChatHistory.mapper;

import com.iecube.community.model.AiChatHistory.entity.AiChatHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiChatHistoryMapper {

    int insert(AiChatHistory record);
}
