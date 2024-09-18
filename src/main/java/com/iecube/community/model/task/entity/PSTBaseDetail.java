package com.iecube.community.model.task.entity;

import lombok.Data;

@Data
public class PSTBaseDetail {
    Integer projectId;
    String projectName;
    Integer deviceId;
    Integer pstId;
    Double grade;
    Integer status;
    Integer taskNum;
    String taskName;
    String studentId;
    String studentName;
}
