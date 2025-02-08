package com.iecube.community.model.elaborate_md.course.entity;

import lombok.Data;

@Data
public class CourseEntity {
    private long id;
    private String name;
    private boolean hasChildren;
    private long sort=0L;
    private int level=0;
}
