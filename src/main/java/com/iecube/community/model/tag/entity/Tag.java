package com.iecube.community.model.tag.entity;

import lombok.Data;

@Data
public class Tag {
    Integer id;
    String name;
    String suggestion;
    Integer projectId;
    Integer taskNum;
    Integer teacherId;
}
