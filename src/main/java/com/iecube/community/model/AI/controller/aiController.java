package com.iecube.community.model.AI.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.AI.service.AiService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class aiController extends BaseController {

    @Autowired
    private AiService aiService;

    @PostMapping("/assistant/chat")
    public JsonResult<String> assistantChat(Integer taskId){
        Integer studentId = currentUserId();
        String chatId = aiService.getAssistantChatId(studentId, taskId);
        return new JsonResult<>(OK, chatId);
    }
}
