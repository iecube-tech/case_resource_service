package com.iecube.community.model.elaborate_md_task.dto;

import lombok.Data;

@Data
public class EMDStuTaskDetailDto {
    private Long stuTaskId;
    private Long stsId;
    private Integer stsStatus;
    private Integer stsSort;
    private Long stsBlockId;
    private Integer stsBlockResult;
    private String stsBlockStuData;
    private Long blockId;
    private Integer blockSort;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String confData;
    private String referenceData;
    private String dataTemplate;
}
