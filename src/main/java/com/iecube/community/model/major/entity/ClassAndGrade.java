package com.iecube.community.model.major.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class ClassAndGrade extends BaseEntity {
    Integer id;
    Integer grade;
    String name;
    Integer majorId;
}
