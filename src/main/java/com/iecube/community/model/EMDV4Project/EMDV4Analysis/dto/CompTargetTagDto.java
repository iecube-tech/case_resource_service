package com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto;

import lombok.Data;

@Data
public class CompTargetTagDto {
    private Long pstId;
    private String studentId;
    private String studentName;
    private Long psId;
    private Long ptId;
    private String ptName;
    private String compId;
    private String compType;
    private Integer compStage;
    private Integer compStatus; // 做过为1 没做过为0
    private String compName;
    private Double compScore;
    private Double compTotalScore;
    private String compPayload;
    private Integer tagId;
    private String tagName;
    private Integer targetId;
    private boolean keyNode;
    private String targetName;
    private String targetDesc;
    private String targetPName;
    private String targetRName;
}
