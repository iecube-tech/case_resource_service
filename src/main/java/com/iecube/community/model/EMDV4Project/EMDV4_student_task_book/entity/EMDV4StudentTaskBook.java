package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import lombok.Data;

import java.util.List;

@Data
public class EMDV4StudentTaskBook {
    private String id;
    private String pId;
    private Long sourceId;
    private Integer stage;
    private Integer level;
    private Integer version;
    private String name;
    private String type;
    private String description;
    private Integer order;
    private String sectionPrefix;
    private Long deviceType;
    private String icon;
    private Boolean stepByStep;
    private String style;
    private String config;
    private String payload;
    private Integer status;
    private Boolean hasChildren;
    private Integer currentChild;
    private List<EMDV4Component> components;
    private List<EMDV4StudentTaskBook> children;
    private List<Long> tagList; //统计实验指导书下的tag数量
}
