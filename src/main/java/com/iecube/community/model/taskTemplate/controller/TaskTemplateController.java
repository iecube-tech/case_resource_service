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
    public JsonResult<List> addTaskTemplateToContent(@RequestBody TaskTemplateDto taskTemplateDto){
        Integer user = currentUserId();
        taskTemplateService.addTaskTemplateToContent(taskTemplateDto, user);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(taskTemplateDto.getContentId());
        return new JsonResult<>(OK,taskTemplateDtos);
    }

    @PostMapping("/update")
    public JsonResult<List> updateTaskTemplate(@RequestBody TaskTemplateDto taskTemplateDto){
        Integer user = currentUserId();
        taskTemplateService.updateTaskTemplate(taskTemplateDto, user);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(taskTemplateDto.getContentId());
        return new JsonResult<>(OK,taskTemplateDtos);
    }

    @GetMapping("/delete")
    public JsonResult<Void> deleteTaskTemplateById(Integer taskTemplateId){
        Integer user = currentUserId();
        taskTemplateService.deleteTaskTemplateByID(taskTemplateId, user);
        return new JsonResult<>(OK);
    }

    @GetMapping("/delete/{caseId}")
    public JsonResult<List> deleteTaskTemplateById(@PathVariable Integer caseId,Integer taskTemplateId){
        Integer user = currentUserId();
        taskTemplateService.deleteTaskTemplateByID(taskTemplateId, user);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(caseId);
        return new JsonResult<>(OK,taskTemplateDtos);
    }

    @PostMapping("/task_template_add_resource/{caseId}/{taskTemplateId}")
    public JsonResult<List> taskTemplateAddResource(MultipartFile file,@PathVariable Integer caseId,@PathVariable Integer taskTemplateId) throws IOException {
        Integer creator = currentUserId();
        Resource resource = resourceService.UploadFile(file, creator);
        taskTemplateService.addResourceToTaskTemplate(taskTemplateId,resource);
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(caseId);
        return new JsonResult<>(OK,taskTemplateDtos);
    }

}
