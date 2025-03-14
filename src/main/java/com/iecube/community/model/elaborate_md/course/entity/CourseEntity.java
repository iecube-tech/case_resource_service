package com.iecube.community.model.elaborate_md.course.entity;

import lombok.Data;

@Data
public class CourseEntity {
    private long id;
    private String name;
    private boolean hasChildren;
    private long sort=0L;
    private int level=0;
    private String treeId;

    // 无参构造函数
    public CourseEntity() {
    }

    // 有参构造函数
    public CourseEntity(long id, String name, boolean hasChildren, int sort, int level) {
        this.id = id;
        this.level = level;
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
