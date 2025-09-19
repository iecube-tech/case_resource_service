package com.iecube.community.model.EMDV4.BookLab.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iecube.community.model.EMDV4.LabComponent.entity.LabComponent;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BookLabCatalog {
    private Long id;
    @JsonProperty("pId")
    private Long pId;
    private Integer level;
    private Integer version;
    private Integer order;
    private String type;
    private String description;
    private Integer stage;
    private Long deviceType;
    private Boolean stepByStep;
    private String name;
    private String sectionPrefix;
    private String icon;
    private String style;
    private String config;
    private String payload;
    private Boolean needPassScore;
    private Double passScore;
    private Boolean hasChildren;
    private Instant createdAt;
    private Instant updatedAt;
    private double weighting;
    private List<LabComponent> componentList;
    private List<BookLabCatalog> children;
}
