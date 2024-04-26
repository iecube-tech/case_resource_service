package com.iecube.community.model.project_student_group.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Group extends BaseEntity {
    Integer id;
    String name;
    Integer limitNum;
    Integer projectId;
    Integer submitted;
}
