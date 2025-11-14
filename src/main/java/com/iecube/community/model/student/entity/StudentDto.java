package com.iecube.community.model.student.entity;

import lombok.Data;

@Data
public class StudentDto {
    private Integer id;
    private String email;
    private String studentId;
    private String studentName;
    private Integer studentGrade;
    private String studentClass;
    private Integer gradeClass;
    private String major;
    private String collage;
    private String school;
}
