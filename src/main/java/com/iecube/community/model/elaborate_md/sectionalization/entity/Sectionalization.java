package com.iecube.community.model.elaborate_md.sectionalization.entity;

import lombok.Data;

@Data
public class Sectionalization {
    private long id;
    private long parentId; //LabProcId
    private int sort;
    private boolean hasChildren;
    private String name="分节";
    private int level=2;
}
