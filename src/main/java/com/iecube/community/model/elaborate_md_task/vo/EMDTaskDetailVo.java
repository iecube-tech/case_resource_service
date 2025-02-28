package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

import java.util.List;

@Data
public class EMDTaskDetailVo {
    private Long labProcId;
    private List<EMDTaskSectionVo> sectionVoList;
}
