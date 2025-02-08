package com.iecube.community.model.elaborate_md.compose.entity;

import lombok.Data;

@Data
public class Compose {
    private long id;
    private long parentId; // 外键 BlockId
    private int sort;
    private boolean hasChildren=false;
    private String name="内容";
    private int level=4;
}
