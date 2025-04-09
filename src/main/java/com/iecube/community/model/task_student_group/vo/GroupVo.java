package com.iecube.community.model.task_student_group.vo;

import com.iecube.community.model.student.entity.Student;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GroupVo {
    Integer groupId;
    String groupName;
    Integer limitNum;
    Integer creator;
    String code;
    Integer submitted;
    Date codeUnableTime;
    List<Student> groupStudents;
}
