package com.iecube.community.model.elaborate_md_task.vo;

import lombok.Data;

@Data
public class EMDTaskBlockVo {
    private Long blockId;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String confData;
    private String referenceData;
    private String dataTemplate;
    private String stuData;
    private Integer result;
}
