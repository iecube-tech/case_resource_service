package com.iecube.community.model.student.dto;

import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.teacher.entity.Teacher;
import lombok.Data;

@Data
public class LoginDto {
    private String token;
    private StudentDto studentDto;
    private Teacher teacher;
    private Teacher user;
}