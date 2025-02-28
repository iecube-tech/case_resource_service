package com.iecube.community.model.project.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProjectStudent extends BaseEntity {
    private Integer id;
    private Integer projectId;
    private Integer studentId;
    private Double grade;
    private List<String> tags;
    private String evaluate;
    private String improvement;
    private List<String> imgs;
    private List<String> files;
    private String video;
    private String content;
}
