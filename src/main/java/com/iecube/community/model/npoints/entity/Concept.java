package com.iecube.community.model.npoints.entity;

import lombok.Data;

@Data
public class Concept {
    Integer conceptId;
    String name;
    Integer moduleId;
    Integer caseId;
    Integer x;
    Integer y;
    ItemStyle itemStyle;
    Emphasis emphasis;
    Label label;
}
