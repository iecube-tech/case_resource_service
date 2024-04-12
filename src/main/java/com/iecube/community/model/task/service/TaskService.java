package com.iecube.community.model.task.service;

import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.task.entity.*;
import com.iecube.community.model.task.vo.TaskBriefVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskService {
    Integer createTask(Task task, Integer user);

    List<ProjectStudentTask> getPSTByProjectId(Integer projectId);

    List<StudentTaskDetailVo> findStudentTaskByProjectId(Integer projectId, Integer studentId);

    List<PSTResourceVo> findPSTResourceVo(Integer pstId);

    StudentTaskDetailVo findStudentTaskByPSTId(Integer pstId);

    Void teacherModifyPST(ProjectStudentTaskQo projectStudentTaskQo);
    Void autoComputeProjectGrade(Integer pstId);

    Void teacherReadOverStudentSubmitPdf(MultipartFile file, String filename, Integer pstId, Integer teacherId) throws IOException;

    List<TaskVo> studentGetProjectTasks(Integer projectId);

    StudentTaskDetailVo submitFile(MultipartFile file, Integer pstId, Integer studentId) throws IOException;

    StudentTaskDetailVo deleteStudentSubmitFile(Integer PSTResourceId, Integer studentId);

    StudentTaskDetailVo studentSubmitContent(String content, Integer pstId,Integer studentId);

    StudentTaskDetailVo studentChangeStatus(Integer pstId);

    List<TaskVo> getProjectTasks(Integer projectId);

    Void updateDataTables(Integer pstId, String dataTables);

    List<TaskBriefVo> getProjectTaskBriefList(Integer projectId);
}
