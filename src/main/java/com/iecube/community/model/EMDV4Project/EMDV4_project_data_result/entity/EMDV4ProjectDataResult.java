package com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.entity;

import lombok.Data;

@Data
public class EMDV4ProjectDataResult {
    private Long id;
    private Integer projectId;
    private Integer progress;
    private Double taskCompletionRate;
    private Double tagAchievementRate;
    private Double averageScore;
    private Double passRate;
    private Double aiUseRate;
    private Double degreeOfAchievementOfCourseTarget;
}
