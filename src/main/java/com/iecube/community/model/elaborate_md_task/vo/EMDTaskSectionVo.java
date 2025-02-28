package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

import java.util.List;

@Data
public class EMDTaskSectionVo {
    private Long STSid;
    private Long sectionId;
    private Integer status;
    private List<EMDTaskBlockVo> blockVoList;
}
