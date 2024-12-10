package com.iecube.community.model.teacher.dto;

import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.teacher.entity.Teacher;
import lombok.Data;

import java.util.List;

@Data
public class LoginDto {
    private String token;
    private StudentDto studentDto;
    private Teacher teacher;
    private List<String> authList;
}
