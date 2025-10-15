package com.iecube.community.model.EMDV4Project.project.service;

import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.vo.EMDV4ProjectStudentVo;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.project.entity.Project;

import java.util.List;

public interface EMDV4ProjectService {

    Integer addProject(EMDV4ProjectQo emdv4ProjectQo,Integer teacherId);

    List<Project> stuProject(Integer studentId);

    Project getProject(Integer projectId);

    List<EMDV4ProjectStudentVo> addStudentToProject(List<Integer> studentIds, Integer projectId);
}
