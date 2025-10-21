package com.iecube.community.model.taskTemplate.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class TaskTemplate extends BaseEntity {
    private Integer id;
    private Integer contentId;
    private Integer num; //用于一个项目下的任务排序
    private Double weighting;
    private Double classHour;
    private String taskName;
    private String taskCover;
    private Integer taskDevice;
    private Boolean step1NeedPassScore;
    private Double step1PassScore;
    private Integer version;
    private Boolean useCoder;
    private Boolean useLabProc;
    private String coderType;
}
