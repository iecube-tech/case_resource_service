package com.iecube.community.model.duplicate_checking.dto;

import lombok.Data;

@Data
public class TaskStudentPDFFile {
    Integer projectId;
    Integer taskId;
    Integer studentId;
    Integer pstId;
    Integer resourceId;
    String fileName;
}
