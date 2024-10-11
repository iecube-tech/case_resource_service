package com.iecube.community.model.task.controller;

import com.iecube.community.basecontroller.task.TaskBaseController;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.project_student_group.entity.Group;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.task.entity.*;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.model.task.vo.TaskBriefVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends TaskBaseController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectMapper projectMapper;

    @GetMapping
    public JsonResult<List> Tasks(Integer projectId, Integer studentId){
        if(studentId==null){
            studentId = currentUserId();
        }
        List<StudentTaskDetailVo> tasks = taskService.findStudentTaskByProjectId(projectId, studentId);
        return new JsonResult<>(OK,tasks);
    }

    /**
     * 教师端请求学生的提交和以批阅的报告
     * @param pstId
     * @return
     */
    @GetMapping("/pst_report/{pstId}")
    public JsonResult<List> getStudentReports(@PathVariable Integer pstId){
        List<PSTResourceVo> list = taskService.findPSTResourceVo(pstId);
        return new JsonResult<>(OK, list);
    }

    @PostMapping("/pst")
    public JsonResult<Void> teacherModifyPST(@RequestBody ProjectStudentTaskQo projectStudentTaskQo){
        taskService.teacherModifyGroupPST(projectStudentTaskQo);
        return new JsonResult<>(OK);
    }

    /**
     * 教师对学生提交的pdf文档进行批阅 或对已批阅的文档进行修改
     * @param file 教师提交的文档
     * @param filename 原学生提交的文档的文件名
     * @param pstRId pstResourceId
     * @return
     */
    @PostMapping("/readover")
    public JsonResult<Void> teacherReadOverStudentSubmitPdf(@RequestBody MultipartFile file,
                                String filename,Integer pstRId) throws IOException {
        Integer teacherId = currentUserId();
        taskService.teacherReadOverGroupSubmitPdf(file, pstRId, teacherId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/submitfile")
    public JsonResult<StudentTaskDetailVo> studentSubmitFile(MultipartFile file, Integer pstId) throws IOException{
        Integer studentId = currentUserId();
        StudentTaskDetailVo taskDetail = taskService.submitFile(file, pstId, studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/deletesubmitfile")
    public JsonResult<StudentTaskDetailVo> deleteStudentSubmitFile(Integer PSTResourceId){
        Integer studentId = currentUserId();
        StudentTaskDetailVo taskDetail =  taskService.deleteStudentSubmitFile(PSTResourceId, studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    /**
     * 学生点击保存按钮触发
     * @param content
     * @param pstId
     * @return
     */
    @PostMapping("/submitcontent")
    public JsonResult<StudentTaskDetailVo> studentSubmitContent(String content, Integer pstId){
        Integer studentId = currentUserId();
        StudentTaskDetailVo taskDetail = taskService.studentSubmitContent(content,pstId,studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    @PostMapping("/submit")
    public JsonResult<StudentTaskDetailVo> studentSubmit(Integer pstId){
        Integer studentId = currentUserId();
        StudentTaskDetailVo taskDetail =taskService.studentSubmit(pstId, studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/taskdetail")
    public JsonResult<StudentTaskDetailVo> getTaskDetailByPSTId(Integer pstId){
        Integer studentId = currentUserId();
        StudentTaskDetailVo taskDetail = taskService.findStudentTaskByPSTId(pstId);
        return new JsonResult<>(OK,taskDetail);
    }

    @GetMapping("/changestatus")
    public JsonResult<StudentTaskDetailVo> studentChangeStatus(Integer pstId){
        Integer studentId= currentUserId();
        StudentTaskDetailVo taskDetail = taskService.studentChangeStatus(pstId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/project_tasks")
    public JsonResult<List> getProjectTasks(Integer projectId){
        List<TaskVo> taskVoList = taskService.getProjectTasks(projectId);
        return new JsonResult<>(OK,taskVoList);
    }

    @PostMapping("/up_dt")
    public JsonResult<List> updateDataTables(@RequestBody PSTDataTables pstDataTables){
        taskService.updateDataTables(pstDataTables.getPstId(), pstDataTables.getDataTables());
        return new JsonResult<>(OK);
    }

    @GetMapping("/brief")
    public JsonResult<List> getProjectTaskBriefList(Integer projectId){
        List<TaskBriefVo> result = taskService.getProjectTaskBriefList(projectId);
        return new JsonResult<>(OK, result);
    }

    @GetMapping("/psts/{projectId}")
    public JsonResult<List> getPSTBaseDetailByProject(@PathVariable Integer projectId){
        List<PSTBaseDetail> pstBaseDetailList = taskService.getPSTBaseDetailByProject(projectId);
        return new JsonResult<>(OK, pstBaseDetailList);
    }

    @PostMapping("/md/readover/{pstId}")
    public JsonResult<PSTBaseDetail> teacherReadOverMdTask(@PathVariable Integer pstId){
        Integer teacherId = currentUserId();
        PSTBaseDetail pstBaseDetail =null;
        Project project = projectMapper.findByPstId(pstId);
        if(project.getUseGroup() == 1 && project.getMdCourse() != null){
            pstBaseDetail = taskService.mdGroupReadOverPSTArticle(project.getId(), pstId, teacherId);
            taskService.genMdArticleReportGroup(project.getId(), pstId, teacherId);
        }
        else {
            pstBaseDetail = taskService.noGroupReadOverPSTArticle(pstId,teacherId);
            taskService.genMdArticleReport(pstId,pstBaseDetail, teacherId);
        }
        return new JsonResult<>(OK, pstBaseDetail);
    }
}
