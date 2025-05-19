package com.iecube.community.model.AI.mapper;

import com.iecube.community.model.AI.entity.AiAssistant;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiAssistantMapper {
    int insert(AiAssistant aiAssistant);

    AiAssistant getChatIdByStuTask(Integer studentId, Integer taskId, String type);

    AiAssistant getAiAssistantByChatId(String chatId);
}
