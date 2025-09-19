package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.EMDV4ProjectStudentService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.vo.EMDV4ProjectStudentVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emdv4/project/student_task/")
public class EMDV4ProjectStudentController extends BaseController {

    @Autowired
    private EMDV4ProjectStudentService emdV4ProjectStudentService;


    /**
     * 获取发布课程下的学生列表，以及每个学生的实验列表
     * @param projectId 已发布课程的id
     * @return List<EMDV4ProjectStudent>
     */
    @GetMapping("/students")
    public JsonResult<List<EMDV4ProjectStudentVo>> getProjectStudentList(Integer projectId) {
        return new JsonResult<>(OK, emdV4ProjectStudentService.getProjectStudentListByProjectId(projectId));
    }

    @GetMapping("/ps_by_ptid")
    public JsonResult<List<EMDV4ProjectStudentVo>> getProjectStudentListByPTid(Long projectTaskId) {
        return new JsonResult<>(OK, emdV4ProjectStudentService.getProjectStudentListByPTid(projectTaskId));
    }
}
