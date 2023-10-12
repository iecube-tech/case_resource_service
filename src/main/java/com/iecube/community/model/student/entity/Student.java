package com.iecube.community.model.student.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Student extends BaseEntity {
    Integer id;
    String email;
    String studentId;
    String studentName;
    Integer studentGrade;
    String studentClass;
    Integer majorId;
    Integer status;
    String password;
    String salt;
}
