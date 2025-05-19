package com.iecube.community.model.Ai2830.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.Ai2830.service.Ai2830Service;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aiofcontrol/2830/")
public class Ai2830Controller extends BaseController {

    @Autowired
    private Ai2830Service ai2830Service;

    @GetMapping("/chat")
    public JsonResult<String> getChatId(Integer taskId){
        Integer studentId = currentUserId();
        String chatId = ai2830Service.getChatId(studentId,taskId,"chat");
        return new JsonResult<>(OK, chatId);
    }
}
