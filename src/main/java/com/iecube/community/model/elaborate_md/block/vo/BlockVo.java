package com.iecube.community.model.elaborate_md.block.vo;

import lombok.Data;

@Data
public class BlockVo {
    private long blockId;
    private long sectionId;
    private int sort;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String confData;
    private String payload;
    private String styleType;
}
