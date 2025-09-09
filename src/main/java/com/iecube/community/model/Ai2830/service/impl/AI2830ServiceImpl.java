package com.iecube.community.model.Ai2830.service.impl;

import com.iecube.community.model.AI.entity.AiAssistant;
import com.iecube.community.model.AI.mapper.AiAssistantMapper;
import com.iecube.community.model.Ai2830.api.service.Ai2830ApiService;
import com.iecube.community.model.Ai2830.service.Ai2830Service;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AI2830ServiceImpl implements Ai2830Service {

    @Autowired
    private AiAssistantMapper aiAssistantMapper;

    @Autowired
    private Ai2830ApiService ai2830ApiService;

    @Override
    public String getChatId(Integer studentId, Long taskId, String type, Integer version) {
        AiAssistant aiAssistant = aiAssistantMapper.getChatIdByStuTask(studentId, taskId, type, version);
        if(aiAssistant != null) {
            return aiAssistant.getChatId();
        }
        else {
            String chatId = ai2830ApiService.register();
            AiAssistant assistant = new AiAssistant();
            assistant.setChatId(chatId);
            assistant.setStudentId(studentId);
            assistant.setTaskId(taskId);
            assistant.setType(type);
            int res = aiAssistantMapper.insert(assistant);
            if(res!=1){
                throw new InsertException("新增数据异常");
            }
            return chatId;
        }
    }

    public static String generateUniqueString(int a, int b) {
        // 将两个int值转换为字节数组
        byte[] bytes = new byte[8];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (a >> (i * 8));
        }
        for (int i = 0; i < 4; i++) {
            bytes[i + 4] = (byte) (b >> (i * 8));
        }
        // 使用Base64编码确保生成的字符串是合法的字符串
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Override
    public Boolean chatIdExist(String chatId) {
        AiAssistant assistant = aiAssistantMapper.getAiAssistantByChatId(chatId);
        return assistant!=null;
    }
}
