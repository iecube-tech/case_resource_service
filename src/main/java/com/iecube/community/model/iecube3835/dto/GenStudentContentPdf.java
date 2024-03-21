package com.iecube.community.model.iecube3835.dto;

import com.iecube.community.model.student.entity.StudentDto;
import lombok.Data;

@Data
public class GenStudentContentPdf {
    StudentDto studentDto;
    StudentSubmitContentDetails studentSubmitContentDetails;
}
