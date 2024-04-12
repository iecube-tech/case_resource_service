package com.iecube.community.model.pst_devicelog.dto;

import lombok.Data;

@Data
public class StudentLogOverview {
    String studentName;
    String studentId;
    Integer pstId;
    Integer taskId;
    Integer taskNum;
    String times;
    String operations;
}