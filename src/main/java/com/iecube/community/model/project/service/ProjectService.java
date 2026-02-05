package com.iecube.community.model.project.service;

import com.iecube.community.model.project.entity.*;
import com.iecube.community.model.student.entity.StudentDto;

import java.io.File;
import java.util.List;

public interface ProjectService {
    Integer addProject(ProjectDto project, Integer creator);

    List<Project> myProject(Integer teacherId);

    List<Project> myProjectNotDel(Integer teacherId);

    List<ProjectStudentVo> projectStudentAndStudentTasks(Integer projectId);

    List<Project> findProjectByStudentId(Integer id);

    List<Project> findCourseByStudentId(Integer id);

    StudentProjectVo studentProjectDetail(Integer projectId);

    Project findProjectById(Integer id);

    File downloadStudentReport(Integer projectId, Integer studentId);

    File downloadProjectReport(Integer projectId);

    File exportProjectData(Integer projectId);

    File ReGenerateProjectData(Integer projectId);

    void deleteProject(Integer projectId);

    void hiddenProject(Integer projectId);

    //学生自己加入project
    Integer studentJoinProject(Integer projectId, Integer studentId);

    List<StudentDto> getProjectStudents(Integer projectId);

    void createRemote(Project project, RemoteQo remoteQo);

    Project publishGrade(Integer projectId);

    List<Project> getStudentGrades(Integer studentId);

    List<Project> getMyWhichCreatedExam(Integer teacherId);
}
