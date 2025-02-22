package com.iecube.community.model.elaborate_md.block.entity;

import lombok.Data;

@Data
public class BlockDetail {
    private long id;
    private long parentId;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String confData;
    private String referenceData;
    private String dataTemplate;
}
