package com.iecube.community.model.elaborate_md.lab_proc.entity;

import lombok.Data;

@Data
public class LabProc {
    private long id;
    private long parentId; // 外键
    private String name;
    private boolean hasChildren;
    private int sort; // 排序
    private int level=1;
}
