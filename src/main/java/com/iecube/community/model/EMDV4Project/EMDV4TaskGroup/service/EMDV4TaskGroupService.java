package com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service;


import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.qo.TaskGroupQo;
import com.iecube.community.model.student.entity.Student;

import java.util.List;

public interface EMDV4TaskGroupService {

    EMDV4TaskGroup getTaskStudentGroup(Long taskId, Integer studentId);

    EMDV4TaskGroup createTaskGroup(TaskGroupQo taskGroupQo, Integer studentId);

    EMDV4TaskGroup taskGroupFreshCode(Long groupId, Integer studentId);

    EMDV4TaskGroup taskGroupSetDoneStatus(Long groupId, Integer studentId);

    EMDV4TaskGroup deleteTaskGroup(Long id, Integer studentId);

    EMDV4TaskGroup addStudentsToTaskGroup(TaskGroupQo taskGroupQo, Integer studentId);

    List<Student> hasNotJoinedGroupStudent(Long taskId);

    EMDV4TaskGroup removeStudentsFromTaskGroup(Long groupId, Integer stuId, Integer studentId);

    EMDV4TaskGroup joinGroup(Long taskId, String code, Integer studentId);
}
