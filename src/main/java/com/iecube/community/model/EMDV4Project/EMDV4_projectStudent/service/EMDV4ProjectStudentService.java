package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service;

import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.student.entity.StudentDto;

import java.util.List;

public interface EMDV4ProjectStudentService {
    List<EMDV4ProjectStudent> createProjectStudents(Project project, EMDV4ProjectQo emdv4ProjectQo, List<StudentDto> studentDtoList);
    void updateProjectStudentTotalNum(Integer projectId, int totalNumOfLabs, int totalNumOfTags);
    EMDV4ProjectStudent getByStuProject(int studentId, int projectId);
}
