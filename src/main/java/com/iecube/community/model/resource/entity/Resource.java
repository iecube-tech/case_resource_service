package com.iecube.community.model.resource.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;


@Data
public class Resource extends BaseEntity {
    Integer id;
    String name;
    String filename;
    String originFilename;
    String type;
}
