package com.iecube.community.model.elaborate_md.block.entity;

import lombok.Data;

@Data
public class Block {
    private long id;
    private long parentId; // 外键 sectionId
    private int sort;
    private boolean hasChildren;
    private String name="块";
    private int level=3;
}
