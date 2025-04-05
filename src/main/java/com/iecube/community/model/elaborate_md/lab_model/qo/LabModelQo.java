package com.iecube.community.model.elaborate_md.lab_model.qo;

import lombok.Data;

@Data
public class LabModelQo {
    private long id;
    private Long labProcId;
    private String name;
    private String icon;
    private String sectionPrefix; // ai知识库对应的章节序号
    private Boolean isNeedAiAsk;  // 章节结束后 要进行ai提问
    private Integer askNum; // 需要提问几个问题
    private String stage;  // 标识课前or课后  before-class after-class
    private int sort;  // 服务端判断排序的数字
}
