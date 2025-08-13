package com.iecube.community.model.EMDV4.CourseTarget.entity;

import lombok.Data;

import java.util.List;

@Data
public class CourseTarget {
    private Long id;
    private Long MF;
    private Long pId;
    private Integer level;
    private String name;
    private String description;
    private String style;
    private String config;
    private String payload;
    private Integer depth;
    private String path;
    private List<CourseTarget> children;
}
