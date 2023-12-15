package com.iecube.community.model.duplicate_checking.entity;

import lombok.Data;

@Data
public class RepetitiveRate {
    Integer id;
    Integer projectId;
    Integer taskId;
    Integer studentId;
    Integer pstId;
    Integer resourceId;
    String fileName;
    Integer contrastPstId;
    Integer contrastStudentId;
    Integer contrastResourceId;
    String contrastFileName;
    Double repetitiveRate;
    String repetitiveContent;
}
