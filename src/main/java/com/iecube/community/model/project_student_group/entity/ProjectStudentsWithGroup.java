package com.iecube.community.model.project_student_group.entity;

import lombok.Data;

@Data
public class ProjectStudentsWithGroup {
    Integer id;  //Student表库id
    String studentId;
    String studentName;
    Integer groupId;
    String groupName;
}
