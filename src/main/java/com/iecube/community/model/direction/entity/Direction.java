package com.iecube.community.model.direction.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Direction extends BaseEntity {
    private Integer id;
    private String name;
    private Integer pmId;
    private Integer productionGroup;
    private Integer clientele;
}
