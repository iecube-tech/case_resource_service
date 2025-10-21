package com.iecube.community.model.EMDV4.TagLink.entity;

import lombok.Data;

@Data
public class TagLink {
    private Long id;
    private Long tagId;
    private String name;
    private String link;
    private String style;
    private String config;
    private String payload;
}
