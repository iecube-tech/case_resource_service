package com.iecube.community.model.elaborate_md.lab_proc.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabProc {
    private long id;
    private long parentId; // 外键
    private String name;
    private String sectionPrefix; // ai知识库对应的章节序号
    private boolean hasChildren;
    private int sort; // 排序
    private int level=1;
    private String treeId;

    // 无参构造函数
    public LabProc() {
    }

    // 有参构造函数
    public LabProc(long id, long parentId, String name, boolean hasChildren, int sort, int level) {
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
