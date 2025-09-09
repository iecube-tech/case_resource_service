package com.iecube.community.model.AI.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.AI.aiClient.service.AiApiService;
import com.iecube.community.model.AI.ex.AiAPiResponseException;
import com.iecube.community.model.AI.qo.AgentQo;
import com.iecube.community.model.AI.service.AiService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/ai")
public class aiController extends BaseController {

    @Autowired
    private AiService aiService;

    @Autowired
    private AiApiService apiService;

    static final String bookId="5S8NzabuYiO0KgMn1QtXKL";

    @PostMapping("/assistant/chat")
    public JsonResult<String> assistantChat(Long taskId, Integer version){
        Integer studentId = currentUserId();
        String chatId = aiService.getAssistantChatId(studentId, taskId, "chat", version);
        return new JsonResult<>(OK, chatId);
    }

    @PostMapping("/check/chat")
    public JsonResult<String> checkChat(Long taskId, Integer version){
        Integer studentId = currentUserId();
        String chatId = aiService.getAssistantChatId(studentId, taskId, "check", version);
        return new JsonResult<>(OK, chatId);
    }

    @GetMapping("/st/chat")
    public JsonResult<String> stChat(Integer studentId, Long taskId, Integer version){
        return new JsonResult<>(OK, aiService.getStudentTaskChatId(studentId, taskId, version));
    }

    @PostMapping("/teaching_assistant")
    public JsonResult<Void> useTeachingAssistant(@RequestBody AgentQo agentQo){
//        System.out.println(agentQo);
        if(Objects.equals(agentQo.getChatId(), "") ||agentQo.getChatId()==null){
            throw new AiAPiResponseException("参数错误");
        }
        if(Objects.equals(agentQo.getSectionPrefix(), "") ||agentQo.getSectionPrefix()==null){
            throw new AiAPiResponseException("参数错误");
        }
        if(Objects.equals(agentQo.getStuInput(), "") ||agentQo.getStuInput()==null){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getImgDataurls()==null){
            throw new AiAPiResponseException("参数错误");
        }
        apiService.useTeachingAssistant(agentQo.getChatId(), bookId, agentQo.getSectionPrefix(), agentQo.getImgDataurls(), agentQo.getStuInput());
        return new JsonResult<>(OK);
    }

    @PostMapping("/questioner")
    public JsonResult<Void> useQuestioner(@RequestBody AgentQo agentQo){
//        System.out.println(agentQo);
        if(agentQo.getChatId()==null || Objects.equals(agentQo.getChatId(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        if( agentQo.getScene()==null || Objects.equals(agentQo.getScene(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getAmount()==null || agentQo.getAmount().equals(0)){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getSectionPrefix()==null || Objects.equals(agentQo.getSectionPrefix(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        apiService.useQuestioner(agentQo.getChatId(),bookId,agentQo.getAmount(),agentQo.getScene(), agentQo.getSectionPrefix());
        return new JsonResult<>(OK);
    }

    @PostMapping("/marker")
    public JsonResult<Void> useMarker(@RequestBody AgentQo agentQo){
        if(agentQo.getChatId()==null || Objects.equals(agentQo.getChatId(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getQuestion()==null){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getStuInput()==null || Objects.equals(agentQo.getStuInput(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getSectionPrefix()==null || Objects.equals(agentQo.getSectionPrefix(), "")){
            throw new AiAPiResponseException("参数错误");
        }
        if(agentQo.getImgDataurls()==null){
            throw new AiAPiResponseException("参数错误");
        }
        apiService.useMarker(agentQo.getChatId(),bookId, agentQo.getQuestion(),agentQo.getStuInput(), agentQo.getSectionPrefix(), agentQo.getImgDataurls());
        return new JsonResult<>(OK);
    }

    // 获取消息的json格式
    @GetMapping("/artefact/{artefactId}")
    public JsonResult<JsonNode> artefact(@PathVariable String artefactId, Long taskId, String type){
        Integer studentId = currentUserId();
        JsonNode jsonNode = apiService.getJsonRes(artefactId, studentId, taskId, type);
        return new JsonResult<>(OK, jsonNode);
    }
}
