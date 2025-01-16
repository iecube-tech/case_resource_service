package com.iecube.community.model.elaborate_md.block.entity;

import lombok.Data;

@Data
public class BlockEntity {
    private long id;
    private long parentId; // 外键
    private String type;
    private String title;
    private String content;
}
