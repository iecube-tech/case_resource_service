package com.iecube.community.model.taskTemplate.controller;

import com.iecube.community.basecontroller.taskTemplate.taskTemplateBaseController;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/task_template")
public class TaskTemplateController extends taskTemplateBaseController {

    @Autowired
    private TaskTemplateService taskTemplateService;

    @GetMapping()
    public JsonResult<List> contentTaskTemplates(Integer contentId){
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(contentId);
        return new JsonResult<>(OK, taskTemplateDtos);
    }

    @PostMapping("/add")
    public JsonResult<Void> addTaskTemplateToContent(@RequestBody TaskTemplateDto taskTemplateDto, HttpSession session){
        Integer user = getUserIdFromSession(session);
        taskTemplateService.addTaskTemplateToContent(taskTemplateDto, user);
        return new JsonResult<>(OK);
    }

    @GetMapping("/delete")
    public JsonResult<Void> deleteTaskTemplateById(Integer taskTemplateId, HttpSession session){
        Integer user = getUserIdFromSession(session);
        taskTemplateService.deleteTaskTemplateByID(taskTemplateId, user);
        return new JsonResult<>(OK);
    }
}
