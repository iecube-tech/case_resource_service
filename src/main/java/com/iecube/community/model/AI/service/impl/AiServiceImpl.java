package com.iecube.community.model.AI.service.impl;

import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.entity.AiAssistant;
import com.iecube.community.model.AI.mapper.AiAssistantMapper;
import com.iecube.community.model.AI.service.AiService;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private AiAssistantMapper  aiAssistantMapper;

    @Autowired
    private AiApiService aiApiService;

    @Override
    public String getAssistantChatId(Integer studentId, Long taskId, String type, Integer version) {
        AiAssistant aiAssistant = aiAssistantMapper.getChatIdByStuTask(studentId, taskId, type, version);
        if(aiAssistant != null) {
//            aiApiService.webSocketConnect(aiAssistant.getChatId());
            return aiAssistant.getChatId();
        }
        else {
            String chatId = aiApiService.genChat();
            AiAssistant assistant = new AiAssistant();
            assistant.setChatId(chatId);
            assistant.setStudentId(studentId);
            assistant.setTaskId(taskId);
            assistant.setType(type);
            assistant.setVersion(version);
            int res = aiAssistantMapper.insert(assistant);
            if(res!=1){
                throw new InsertException("新增数据异常");
            }
//            aiApiService.webSocketConnect(chatId);
            return chatId;
        }
    }

    @Override
    public String getStudentTaskChatId(Integer studentId, Long taskId, Integer version) {
        AiAssistant aiAssistant = aiAssistantMapper.getChatIdByStuTask(studentId, taskId, "chat", version);
        if(aiAssistant != null) {
            return aiAssistant.getChatId();
        }
        else {
            return null;
        }
    }

    @Override
    public Boolean chatIdExist(String chatId) {
        AiAssistant assistant = aiAssistantMapper.getAiAssistantByChatId(chatId);
        return assistant!=null;
    }
}
