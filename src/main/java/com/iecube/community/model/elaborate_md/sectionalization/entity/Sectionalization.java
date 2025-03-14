package com.iecube.community.model.elaborate_md.sectionalization.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sectionalization {
    private long id;
    private long parentId; //LabProcId
    private int sort;
    private boolean hasChildren;
    private String name="步骤";
    private int level=2;
    private String treeId;

    // 无参构造函数
    public Sectionalization() {
    }

    // 有参构造函数
    public Sectionalization(long id, long parentId, String name, boolean hasChildren, int sort, int level) {
        this.id = id;
        this.level = level;
        this.parentId = parentId;
        this.name = name;
        this.hasChildren = hasChildren;
        this.sort = sort;
        this.treeId = generateTreeId();
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
