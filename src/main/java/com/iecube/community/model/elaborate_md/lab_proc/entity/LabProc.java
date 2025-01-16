package com.iecube.community.model.elaborate_md.lab_proc.entity;

import lombok.Data;

@Data
public class LabProc {
    private long id;
    private long parentId; // 外键
    private String name;
    private int sort; // 排序
}
