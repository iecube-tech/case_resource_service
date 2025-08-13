package com.iecube.community.model.EMDV4.BookLab.entity;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BookLabCatalog {
    private Long id;
    private Long pId;
    private Integer level;
    private Integer version;
    private Integer order;
    private Long deviceType;
    private Boolean stepByStep;
    private String name;
    private String sectionPrefix;
    private String icon;
    private String style;
    private String config;
    private String payload;
    private Boolean hasChildren;
    private Instant createdAt;
    private Instant updatedAt;
    private List<BookLabCatalog> children;
}
