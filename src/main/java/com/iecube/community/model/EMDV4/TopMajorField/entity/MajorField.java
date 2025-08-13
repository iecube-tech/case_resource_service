package com.iecube.community.model.EMDV4.TopMajorField.entity;

import lombok.Data;

import java.util.List;

@Data
public class MajorField {
    private Long id;
    private Long pId;
    private Integer level;
    private String name;
    private List<MajorField> children;

    public void addChild(MajorField child) {
        this.children.add(child);
    }
}
