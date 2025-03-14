package com.iecube.community.model.elaborate_md.block.entity;

import lombok.Data;

@Data
public class Block {
    private long id;
    private long parentId; // 外键 sectionId
    private int sort;
    private boolean hasChildren=false;
    private int level=3;
    private String type;
    private String name;
    private String treeId;

    // 无参构造函数
    public Block() {
    }

    // 有参构造函数
    public Block(long id, boolean hasChildren, int sort, int level) {
        this.id = id;
        this.level = level;
        this.name = type;
        this.hasChildren = hasChildren;
        this.sort = sort;
        this.treeId = generateTreeId();
    }

    public void setType(String type) {
        this.type = type;
        this.name = type;
    }

    public void setId(long id) {
        this.id = id;
        this.treeId = generateTreeId();
    }

    public void setLevel(int level) {
        this.level = level;
        this.treeId = generateTreeId();
    }

    private String generateTreeId() {
        return level + "-" + id;
    }

}
