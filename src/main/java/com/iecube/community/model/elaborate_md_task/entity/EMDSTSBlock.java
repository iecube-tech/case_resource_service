package com.iecube.community.model.elaborate_md_task.entity;

import lombok.Data;

@Data
public class EMDSTSBlock {
    private Long id;
    private Long STSId;
    private Long blockId;
    private Integer status; // 0  完成状态
    private Integer sort;
    private String type;
    private String title;
    private String content;
    private String catalogue;
    private String payload;
    private String ability; // 能力雷达 预留
    private String aiHelpChatId; //预留
    private String aiReviewChatId; //预留
}
