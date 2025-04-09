package com.iecube.community.model.task_student_group.entity;

import lombok.Data;

@Data
public class TaskStudentsWithGroup {
    Integer id;  //Student表库id
    String studentId;
    String studentName;
    Integer groupId;
    String groupName;
}
