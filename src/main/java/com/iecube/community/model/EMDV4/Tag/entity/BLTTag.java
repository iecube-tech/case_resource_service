package com.iecube.community.model.EMDV4.Tag.entity;

import lombok.Data;

@Data
public class BLTTag {
    private Long id;
    private Long BLTId;
    private String name;
    private String ability;
    private String description;
    private String style;
    private String config;
    private String payload;
    private Boolean keyNode;
}
