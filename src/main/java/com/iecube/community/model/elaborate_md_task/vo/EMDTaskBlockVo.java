package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

@Data
public class EMDTaskBlockVo {
    private Long id;
    private Long STSId;
    private Integer status;
    private Integer sort;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String payload;
}
