package com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity;

import lombok.Data;

import java.util.Date;

@Data
public class EMDV4ProjectTask {
    private Long id;
    private Integer projectId;
    private Long labId;
    private Integer num;
    private String name;
    private Double classhour;
    private Double weighting;
    private Integer coefficientOfDifficulty;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date doneTime;
    private Integer totalNumOfStudent;
    private Integer doneNumOfStudent;
    private Double averageScore;
    private Double averageUseTime;
    private Double averageErrorRate;

}
