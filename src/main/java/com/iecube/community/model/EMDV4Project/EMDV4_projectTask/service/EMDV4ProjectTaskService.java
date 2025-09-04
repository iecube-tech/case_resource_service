package com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service;

import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.student.entity.StudentDto;

import java.util.List;

public interface EMDV4ProjectTaskService {
    List<EMDV4ProjectTask> projectTaskListCreate(Project project, EMDV4ProjectQo emdv4ProjectQo, List<StudentDto> studentDtoList);

    void updateProjectTaskStudentNum(Integer projectId, Integer studentNum);

    EMDV4ProjectTask getById(Long projectTaskId);

    List<EMDV4ProjectTask> getProjectTaskList(Integer projectId);
}
