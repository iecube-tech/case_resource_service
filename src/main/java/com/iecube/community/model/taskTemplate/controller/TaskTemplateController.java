package com.iecube.community.model.taskTemplate.controller;

import com.iecube.community.basecontroller.taskTemplate.taskTemplateBaseController;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/task_template")
public class TaskTemplateController extends taskTemplateBaseController {

    @Autowired
    private TaskTemplateService taskTemplateService;

    @Autowired
    private ResourceService resourceService;

    @GetMapping()
    public JsonResult<List> contentTaskTemplates(Integer contentId){
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(contentId);
        return new JsonResult<>(OK, taskTemplateDtos);
    }

    @PostMapping("/add")
    public JsonResult<List> addTaskTemplateToContent(@RequestBody TaskTemplateDto taskTemplateDto, HttpSession session){
        Integer user = getUserIdFromSession(session);
        taskTemplateService.addTaskTemplateToContent(taskTemplateDto, user);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(taskTemplateDto.getContentId());
        return new JsonResult<>(OK,taskTemplateDtos);
    }

    @GetMapping("/delete")
    public JsonResult<Void> deleteTaskTemplateById(Integer taskTemplateId, HttpSession session){
        Integer user = getUserIdFromSession(session);
        taskTemplateService.deleteTaskTemplateByID(taskTemplateId, user);
        return new JsonResult<>(OK);
    }

    @GetMapping("/delete/{caseId}")
    public JsonResult<List> deleteTaskTemplateById(@PathVariable Integer caseId,Integer taskTemplateId, HttpSession session){
        Integer user = getUserIdFromSession(session);
        taskTemplateService.deleteTaskTemplateByID(taskTemplateId, user);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(caseId);
        return new JsonResult<>(OK,taskTemplateDtos);
    }

    @PostMapping("/task_template_add_resource/{caseId}/{taskTemplateId}")
    public JsonResult<List> taskTemplateAddResource(MultipartFile file,@PathVariable Integer caseId,@PathVariable Integer taskTemplateId, HttpSession session) throws IOException {
        Integer creator = getUserIdFromSession(session);
        Resource resource = resourceService.UploadFile(file, creator);
        taskTemplateService.addResourceToTaskTemplate(taskTemplateId,resource);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(caseId);
        return new JsonResult<>(OK,taskTemplateDtos);
    }
}
