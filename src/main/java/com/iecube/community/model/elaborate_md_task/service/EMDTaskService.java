package com.iecube.community.model.elaborate_md_task.service;

import com.iecube.community.model.elaborate_md_task.vo.EMDTaskVo;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.task.entity.Task;

import java.util.List;

public interface EMDTaskService {

    void EMDTaskPublish(List<Student> studentList, List<Task> taskList);

    List<EMDTaskVo> getEMDTaskVoList(Integer projectId);
}
