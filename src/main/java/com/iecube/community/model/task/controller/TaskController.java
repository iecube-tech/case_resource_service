package com.iecube.community.model.task.controller;

import com.iecube.community.basecontroller.task.TaskBaseController;
import com.iecube.community.model.task.entity.ProjectStudentTaskQo;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;
import com.iecube.community.model.task.entity.TaskVo;
import com.iecube.community.model.task.service.TaskService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends TaskBaseController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public JsonResult<List> Tasks(Integer projectId, Integer studentId ,HttpSession session){
        if(studentId==null){
            studentId = getUserIdFromSession(session);
        }
        List<StudentTaskDetailVo> tasks = taskService.findStudentTaskByProjectId(projectId, studentId);
        return new JsonResult<>(OK,tasks);
    }

    @PostMapping("/pst")
    public JsonResult<Void> teacherModifyPST(@RequestBody ProjectStudentTaskQo projectStudentTaskQo, HttpSession session){
        taskService.teacherModifyPST(projectStudentTaskQo);
        return new JsonResult<>(OK);
    }

    /**
     * 教师对学生提交的pdf文档进行批阅 或对已批阅的文档进行修改
     * @param file 教师提交的文档
     * @param filename 原学生提交的文档的文件名
     * @param pstRId pstResourceId
     * @param session
     * @return
     */
    @PostMapping("/readover")
    public JsonResult<Void> teacherReadOverStudentSubmitPdf(@RequestBody MultipartFile file,
                                String filename,Integer pstRId, HttpSession session) throws IOException {
        Integer teacherId = getUserIdFromSession(session);
        taskService.teacherReadOverStudentSubmitPdf(file, filename, pstRId, teacherId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/submitfile")
    public JsonResult<StudentTaskDetailVo> studentSubmitFile(MultipartFile file, Integer pstId, HttpSession session) throws IOException{
        Integer studentId = getUserIdFromSession(session);
        StudentTaskDetailVo taskDetail = taskService.submitFile(file, pstId, studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/deletesubmitfile")
    public JsonResult<StudentTaskDetailVo> deleteStudentSubmitFile(Integer PSTResourceId, HttpSession session){
        Integer studentId = getUserIdFromSession(session);
        StudentTaskDetailVo taskDetail =  taskService.deleteStudentSubmitFile(PSTResourceId, studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    /**
     * 学生点击保存按钮触发
     * @param content
     * @param pstId
     * @param session
     * @return
     */
    @PostMapping("/submitcontent")
    public JsonResult<StudentTaskDetailVo> studentSubmitContent(String content, Integer pstId, HttpSession session){
        Integer studentId = getUserIdFromSession(session);
        StudentTaskDetailVo taskDetail = taskService.studentSubmitContent(content,pstId,studentId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/taskdetail")
    public JsonResult<StudentTaskDetailVo> getTaskDetailByPSTId(Integer pstId, HttpSession session){
        Integer studentId = getUserIdFromSession(session);
        StudentTaskDetailVo taskDetail = taskService.findStudentTaskByPSTId(pstId);
        return new JsonResult<>(OK,taskDetail);
    }

    @GetMapping("/changestatus")
    public JsonResult<StudentTaskDetailVo> studentChangeStatus(Integer pstId, HttpSession session){
        Integer studentId= getUserIdFromSession(session);
        StudentTaskDetailVo taskDetail = taskService.studentChangeStatus(pstId);
        return new JsonResult<>(OK, taskDetail);
    }

    @GetMapping("/project_tasks")
    public JsonResult<List> getProjectTasks(Integer projectId){
        List<TaskVo> taskVoList = taskService.getProjectTasks(projectId);
        return new JsonResult<>(OK,taskVoList);
    }


}
