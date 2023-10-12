package com.iecube.community.model.pst_resource.entity;

import lombok.Data;

@Data
public class PSTResource {
    Integer id;
    Integer PSTId;
    Integer resourceId;
    Integer readOverResourceId;
}
