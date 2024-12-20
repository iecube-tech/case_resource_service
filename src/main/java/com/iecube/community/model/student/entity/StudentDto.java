package com.iecube.community.model.student.entity;

import lombok.Data;

@Data
public class StudentDto {
    Integer id;
    String email;
    String studentId;
    String studentName;
    String studentGrade;
    String studentClass;
    String major;
    String collage;
    String school;
}
