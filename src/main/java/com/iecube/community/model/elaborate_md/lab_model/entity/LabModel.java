package com.iecube.community.model.elaborate_md.lab_model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabModel {
    private Long id;
    private Long parentId;
    private String name;
    private String sectionPrefix; // ai知识库对应的章节序号
    private Boolean isNeedAiAsk;  // 章节结束后 要进行ai提问
    private Integer askNum;  // 需要提问几个问题
    private String stage;  // 标识课前or课后  before-class after-class
    private String icon;
    private Integer sort;
    private int level=2;
    private String treeId;
    private boolean hasChildren;

    // 无参构造函数
    public LabModel() {
    }
    // 有参构造函数
    public LabModel(long id, long parentId, String name, String icon, int sort, int level, boolean hasChildren) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.icon = icon;
        this.sort = sort;
        this.level = level;
        this.hasChildren = hasChildren;
        this.treeId = generateTreeId();
    }

    public void setId(long id) {
        this.id = id;
        this.treeId = generateTreeId();
    }

    public void setLevel(int level) {
        this.level = level;
        this.treeId = generateTreeId();
    }

    private String generateTreeId() {
        return level + "-" + id;
    }
}
