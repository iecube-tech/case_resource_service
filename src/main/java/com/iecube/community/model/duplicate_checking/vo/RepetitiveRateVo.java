package com.iecube.community.model.duplicate_checking.vo;

import lombok.Data;

@Data
public class RepetitiveRateVo {
    Integer id;
    Integer projectId;
    Integer taskId;
    Integer studentId;
    String studentName;
    Integer pstId;
    Integer resourceId;
    String fileName;
    String originFilename;
    Integer contrastStudentId;
    String contrastStudentName;
    Integer contrastResourceId;
    String contrastFileName;
    String contrastOriginFilename;
    Double repetitiveRate;
    String repetitiveContent;
}
