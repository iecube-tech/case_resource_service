package com.iecube.community.model.auth.dto;

import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.teacher.entity.Teacher;
import lombok.Data;

@Data
public class LoginDto {
    String token;
    StudentDto studentDto;
    Teacher teacher;
    User user;
}
