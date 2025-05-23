package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

import java.util.List;

@Data
public class EMDTaskSectionVo {
    private Long id;
    private Long stmId;
    private Integer status;
    private Integer sort;
    private List<EMDTaskBlockVo> blockVoList;
}
