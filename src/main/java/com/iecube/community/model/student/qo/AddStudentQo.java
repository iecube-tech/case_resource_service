package com.iecube.community.model.student.qo;

import lombok.Data;

@Data
public class AddStudentQo {
    String email;
    String studentId;
    String studentName;
    Integer majorId;
    Integer studentClass;
}
