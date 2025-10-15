package com.iecube.community.model.EMDV4Project.project.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.vo.EMDV4ProjectStudentVo;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.EMDV4Project.project.service.EMDV4ProjectService;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.util.JsonResult;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emdv4/project/")
public class EMDV4ProjectController extends BaseController {

    @Autowired
    private EMDV4ProjectService emdv4ProjectService;

    @PostMapping("/publish")
    public JsonResult<Integer> newProject(@RequestBody EMDV4ProjectQo emdv4ProjectQo){
        int res = emdv4ProjectService.addProject(emdv4ProjectQo, currentUserId());
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/detail")
    public JsonResult<Project> getProject(Integer id){
        return new JsonResult<>(OK, emdv4ProjectService.getProject(id));
    }

    @PostMapping("/add/students/")
    public JsonResult<List<EMDV4ProjectStudentVo>> addStudents(@RequestBody AddStudentQo addStudentQo){
        return new JsonResult<>(OK, emdv4ProjectService.addStudentToProject(addStudentQo.getStudentIds(), addStudentQo.getProjectId()));
    }

    @Getter
    @Setter
    public static class AddStudentQo{
        private Integer projectId;
        private List<Integer> studentIds;
    }
}
