package com.iecube.community.model.project.service;

import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.entity.ProjectDto;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.entity.StudentProjectVo;

import java.io.File;
import java.util.List;

public interface ProjectService {
    Integer addProject(ProjectDto project, Integer creator);

    List<Project> myProject(Integer teacherId);

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
}
