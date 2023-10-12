package com.iecube.community.model.classification.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Classification extends BaseEntity {
    private Integer Id;
    private String name;
    private Integer parentId;
    private Integer productionGroup;
    private Integer clientele;
}
