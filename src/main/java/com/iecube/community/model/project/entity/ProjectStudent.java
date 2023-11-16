package com.iecube.community.model.project.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class ProjectStudent extends BaseEntity {
    Integer id;
    Integer projectId;
    Integer studentId;
    Double grade;
    List<String> tags;
    String evaluate;
    String improvement;
    List<String> imgs;
    List<String> files;
    String video;
    String content;
}
