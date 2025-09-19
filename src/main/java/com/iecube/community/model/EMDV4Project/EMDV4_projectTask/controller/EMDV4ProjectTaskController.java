package com.iecube.community.model.EMDV4Project.EMDV4_projectTask.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service.EMDV4ProjectTaskService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emdv4/project/detail/")
public class EMDV4ProjectTaskController extends BaseController {

    @Autowired
    private EMDV4ProjectTaskService emdV4ProjectTaskService;

    @GetMapping("/tasks")
    public JsonResult<List<EMDV4ProjectTask>> getProjectStudentTasks(Integer projectId){
        return new JsonResult<>(OK, emdV4ProjectTaskService.getProjectTaskList(projectId));
        // emdv4ProjectTask
    }

    @GetMapping("/pt_by_id")
    public JsonResult<EMDV4ProjectTask> getById(Long projectTaskId){
        return new JsonResult<>(OK, emdV4ProjectTaskService.getById(projectTaskId));
    }
}
