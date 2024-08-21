package com.iecube.community.model.task.service;

import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.task.entity.*;
import com.iecube.community.model.task.vo.TaskBriefVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    Task createTask(Task task, Integer user);

    List<ProjectStudentTask> getPSTByProjectId(Integer projectId);

    List<StudentTaskDetailVo> findStudentTaskByProjectId(Integer projectId, Integer studentId);

    List<PSTResourceVo> findPSTResourceVo(Integer pstId);

    StudentTaskDetailVo findStudentTaskByPSTId(Integer pstId);

    Void teacherModifyPST(ProjectStudentTaskQo projectStudentTaskQo);

    void teacherModifyGroupPST(ProjectStudentTaskQo projectStudentTaskQo);
    Void autoComputeProjectGrade(Integer pstId);

    void teacherReadOverStudentSubmitPdf(MultipartFile file, Integer pstId, Integer teacherId) throws IOException;
    void teacherReadOverGroupSubmitPdf(MultipartFile file, Integer pstId, Integer teacherId) throws IOException;

    List<TaskVo> studentGetProjectTasks(Integer projectId);

    StudentTaskDetailVo submitFile(MultipartFile file, Integer pstId, Integer studentId) throws IOException;

    StudentTaskDetailVo deleteStudentSubmitFile(Integer PSTResourceId, Integer studentId);

    StudentTaskDetailVo studentSubmitContent(String content, Integer pstId,Integer studentId);

    StudentTaskDetailVo studentSubmit(Integer pstId,Integer studentId);

    StudentTaskDetailVo studentChangeStatus(Integer pstId);

    List<TaskVo> getProjectTasks(Integer projectId);

    Void updateDataTables(Integer pstId, String dataTables);

    List<TaskBriefVo> getProjectTaskBriefList(Integer projectId);

    List<PSTBaseDetail> getPSTBaseDetailByProject(Integer projectId);

    PSTBaseDetail readOverPSTArticle(Integer pstId, Integer teacherId);

    void genMdArticleReport(Integer pstId, PSTBaseDetail pstBaseDetail, Integer teacherId);
}
