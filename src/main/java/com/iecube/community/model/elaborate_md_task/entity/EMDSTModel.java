package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;
import org.apache.ibatis.annotations.Param;

@Data
public class EMDSTModel {
    private Long id;
    private Long stId;
    private Long modelId;
    private String name;
    private String icon;
    private Integer sort;
    private Integer status;
    private String sectionPrefix; // ai知识库对应的章节序号
    private Boolean isNeedAiAsk;  // 章节结束后 要进行ai提问
    private Integer askNum; //需要ai提问几次
    private Integer currAskNum; // 当前在提问第几次
    private String stage;  // 标识课前or课后
}
