package com.iecube.community.model.project.controller;

import com.iecube.community.basecontroller.project.ProjectBaseController;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectDto;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.entity.StudentProjectVo;
import com.iecube.community.model.project.service.ProjectService;
import com.iecube.community.model.project.service.ex.StudentAlreadyInProject;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController extends ProjectBaseController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ResourceService resourceService;

    @PostMapping("/add")
    public JsonResult<Integer> addProject(@RequestBody ProjectDto projectDto){
        Integer teacherId = currentUserId();
        Integer projectId =  projectService.addProject(projectDto, teacherId);
        return new JsonResult<>(OK,projectId);
    }

    /**
     * 教师端查询自己的项目
     *
     * @return
     */
    @GetMapping("/my")
    public JsonResult<List> myProject(){
        Integer teacherId = currentUserId();
        List<Project> myProjects = projectService.myProject(teacherId);
        return new JsonResult<>(OK, myProjects);
    }

    @GetMapping("/my_all")
    public JsonResult<List> myProjectAll(){
        Integer teacherId = currentUserId();
        List<Project> myProjects = projectService.myProjectNotDel(teacherId);
        return new JsonResult<>(OK, myProjects);
    }



    @GetMapping("/teacher_project")
    public JsonResult<List> myProject(Integer teacherId){
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
     *
     * @return
     */
    @GetMapping("/myproject")
    public JsonResult<List> studentProject(){
        Integer studentId = currentUserId();
        List<Project> projects = projectService.findProjectByStudentId(studentId);
        return new JsonResult<>(OK,projects);
    }

    @GetMapping("/mycourse")
    public JsonResult<List> studentCourse(){
        Integer studentId = currentUserId();
        List<Project> courses = projectService.findCourseByStudentId(studentId);
        return new JsonResult<>(OK, courses);
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
        DownloadUtil.httpDownload(studentReport,studentReport.getName(), response);
    }

    @GetMapping("/project_report")
    public void downloadProjectReport(Integer projectId, HttpServletResponse response){
        File projectReport = projectService.downloadProjectReport(projectId);
        DownloadUtil.httpDownload(projectReport,projectReport.getName(), response);
    }

    @GetMapping("/export_project_data")
    public void exportProjectData(Integer projectId, HttpServletResponse response){
        File file = projectService.ReGenerateProjectData(projectId);
        DownloadUtil.httpDownload(file, file.getName(), response);
    }

    @GetMapping("/delete_project")
    public JsonResult<Void> deleteProject(Integer projectId){
        projectService.deleteProject(projectId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/hidden_project")
    public JsonResult<Void> hiddenProject(Integer projectId){
        projectService.hiddenProject(projectId);
        return new JsonResult<>(OK);
    }

    /**
     * 项目推荐  学生自己加入project
     * 需要projectId studnetId
     */
    @PostMapping("/join_project/{projectId}")
    public JsonResult<Boolean> studentJoinProject(@PathVariable Integer projectId){
        Integer studentId = currentUserId();
        projectService.studentJoinProject(projectId,studentId);
        return new JsonResult<>(OK,Boolean.TRUE);
    }

    /**
     *
     * @param projectId
     * @return
     */
    @PostMapping("/add_student/{projectId}")
    public JsonResult<Boolean> projectAddStudent(@PathVariable Integer projectId, @RequestBody List<Student> students){
        log.info("project {} 添加学生", projectId);
        for(Student student : students){
            try{
                projectService.studentJoinProject(projectId,student.getId());
            }catch (StudentAlreadyInProject e){
                log.info("学生 {} 已在Project中", student.getId());
            }
        }
        return new JsonResult<>(OK,Boolean.TRUE);
    }

    @GetMapping("/students/{projectId}")
    public JsonResult<List> getProjectStudent(@PathVariable Integer projectId){
        List<StudentDto> studentDtoList = projectService.getProjectStudents(projectId);
        return new JsonResult<>(OK,studentDtoList);
    }
}
