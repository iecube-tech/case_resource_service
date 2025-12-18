package com.iecube.community.model.project.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Project extends BaseEntity {
    private Integer id;
    private Integer caseId;
    private Integer caseType;   // case表中的third  区分是 课程还是案例
    private String projectName;
    private String introduction; //简介
    private String introduce; // 介绍
    private String cover;   //封面
    private String target; //项目目标
    private Date startTime;
    private Date endTime;
    private Double grade;
    private Integer deviceId;
    private Integer useGroup;
    private Integer groupLimit;
    private Integer hidden;
    private Integer mdCourse;
    private Integer useRemote;
    private String fourth;
    private String fourthType;
    private Long emdCourse;
    private Integer version;
    private String semester;
    private Integer gradeClass;
    private Boolean showTheGrade;
}
