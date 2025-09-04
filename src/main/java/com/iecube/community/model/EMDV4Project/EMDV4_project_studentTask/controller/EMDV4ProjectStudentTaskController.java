package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.EMDV4ProjectStudentService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service.EMDV4ProjectTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emdv4/task/detail/")
public class EMDV4ProjectStudentTaskController extends BaseController {

    @Autowired
    private EMDV4ProjectStudentTaskService emdV4ProjectStudentTaskService;

    @Autowired
    private EMDV4ProjectTaskService emdV4ProjectTaskService;

    @Autowired
    private EMDV4ProjectStudentService emdV4ProjectStudentService;

    @GetMapping("/task_detail")
    public JsonResult<EMDV4ProjectStudentTask> stuGetTaskDetail(Long projectTaskId){
        EMDV4ProjectTask projectTask = emdV4ProjectTaskService.getById(projectTaskId);
        EMDV4ProjectStudent projectStudent = emdV4ProjectStudentService.getByStuProject(currentUserId(), projectTask.getProjectId());
        EMDV4ProjectStudentTask res = emdV4ProjectStudentTaskService.getByProjectTaskAndProjectStudent(projectTaskId, projectStudent.getId());
        return new JsonResult<>(OK, res);
    }
}
