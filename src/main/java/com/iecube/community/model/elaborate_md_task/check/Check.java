package com.iecube.community.model.elaborate_md_task.check;

import com.iecube.community.model.AI.aiClient.dto.MarkerQuestion;
import lombok.Data;

import java.util.List;

@Data
public class Check {
    private Integer taskId;
    private String sectionPrefix;
    private MarkerQuestion question;
    private String cellId;
    private String cellStuAnswer;
    private List<String> cellStuImgList;
}
