package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;

@Data
public class EMDStudentTaskSection {
    private Long id;
    private Long studentTaskId;
    private Long sectionId;
    private Integer status;
    private Integer sort;
}
