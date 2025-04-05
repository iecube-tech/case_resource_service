package com.iecube.community.model.AI.qo;

import com.iecube.community.model.AI.aiClient.dto.MarkerQuestion;
import lombok.Data;

import java.util.List;

@Data
public class AgentQo {
    private String chatId;
    private String sectionPrefix;  // 章节
    private String stuInput;  // 用户的输入内容
    private List<String> imgDataurls; // 用户上传的图片
    private String scene;  // before-class after-class experiment
    private MarkerQuestion question; // marker 需要的question
    private Integer amount; // questioner 出题数量
}
