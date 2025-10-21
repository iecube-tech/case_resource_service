package com.iecube.community.model.task.entity;

import com.iecube.community.model.tag.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class StudentTaskVo {
    private Integer PSTId;
    private Integer studentId;
    private Integer taskId;
    private Integer taskNum;
    private String taskName;
    private Double taskGrade;
    private Integer taskStatus;
    private Boolean step1NeedPassScore;
    private Double step1PassScore;  // 课前预习通过分数的阈值
    private Integer version;
    private Boolean useCoder;
    private Boolean useLabProc;
    private String coderType;
    private List<Tag> tags;
}
