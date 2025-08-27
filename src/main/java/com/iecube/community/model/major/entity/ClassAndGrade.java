package com.iecube.community.model.major.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class ClassAndGrade extends BaseEntity {
    private Integer id;
    private Integer grade;
    private String name;
    private Integer majorId;
}
