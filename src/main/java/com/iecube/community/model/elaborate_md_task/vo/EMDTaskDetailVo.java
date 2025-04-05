package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

import java.util.List;

@Data
public class EMDTaskDetailVo {
    private Integer taskId;
    private List<EMDTaskModelVo> labModelVoList;
}
