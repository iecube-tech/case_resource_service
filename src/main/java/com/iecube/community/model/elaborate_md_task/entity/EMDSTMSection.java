package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;

@Data
public class EMDSTMSection {
    private Long id;
    private Long stmId;
    private Long sectionId;
    private Integer status;
    private Integer sort;
}
