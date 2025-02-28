package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;

@Data
public class EMDStudentTask {
    private Long id;
    private Integer studentId;
    private Integer taskId;
    private Integer status;
}
