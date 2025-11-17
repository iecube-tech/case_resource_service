package com.iecube.community.model.student.dto;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class AddStudentDto extends BaseEntity {
    Integer id;
    String email;
    String studentId;
    String studentName;
    Integer majorId;
    Integer studentClass;
    Integer status;
    String password;
    String salt;
    String msg;
}
