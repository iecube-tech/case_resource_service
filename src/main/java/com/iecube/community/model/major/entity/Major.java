package com.iecube.community.model.major.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Major extends BaseEntity {
    Integer id;
    String name;
    Integer collageId;
}
