package com.iecube.community.model.project.controller;

import com.iecube.community.basecontroller.project.ProjectBaseController;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectDto;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.entity.StudentProjectVo;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController extends ProjectBaseController {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/add")
    public JsonResult<Integer> addProject(@RequestBody ProjectDto projectDto, HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        Integer projectId =  projectService.addProject(projectDto, teacherId);
        return new JsonResult<>(OK,projectId);
    }

    /**
     * 教师端查询自己的项目
     * @param session
     * @return
     */
    @GetMapping("/my")
    public JsonResult<List> myProject(HttpSession session){
        Integer teacherId = getUserIdFromSession(session);
        List<Project> myProjects = projectService.myProject(teacherId);
        return new JsonResult<>(OK, myProjects);
    }

    @GetMapping("/detail")
    public JsonResult<List> projectDetail(Integer projectId){
        List<ProjectStudentVo> students = projectService.projectStudentAndStudentTasks(projectId);
        return new JsonResult<>(OK,students);
    }

    /**
     * 学生查询自己的项目
     * @param session
     * @return
     */
    @GetMapping("/myproject")
    public JsonResult<List> studentProject(HttpSession session){
        Integer studentId = getUserIdFromSession(session);
        List<Project> projects = projectService.findProjectByStudentId(studentId);
        return new JsonResult<>(OK,projects);
    }

    @GetMapping("/project")
    public JsonResult<Project> GetProject(Integer projectId){
        Project project = projectService.findProjectById(projectId);
        return new JsonResult<>(OK,project);
    }


    @GetMapping("/spdetail")
    public JsonResult<StudentProjectVo> studentProjectDetail(Integer projectId){
        StudentProjectVo studentProject = projectService.studentProjectDetail(projectId);
        return new JsonResult<>(OK, studentProject);
    }

    @GetMapping("/student_report")
    public void downloadStudentReport(Integer projectId, Integer studentId, HttpServletResponse response){
        File studentReport = projectService.downloadStudentReport(projectId,studentId);
        DownloadUtil.httpDownload(studentReport, response);
    }

    @GetMapping("/project_report")
    public void downloadProjectReport(Integer projectId, HttpServletResponse response){
        File projectReport = projectService.downloadProjectReport(projectId);
        DownloadUtil.httpDownload(projectReport, response);
    }
}
