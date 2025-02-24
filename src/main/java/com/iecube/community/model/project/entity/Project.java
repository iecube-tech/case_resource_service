package com.iecube.community.model.project.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class Project extends BaseEntity {
    Integer id;
    Integer caseId;
    Integer caseType;   // case表中的third  区分是 课程还是案例
    String projectName;
    String introduction; //简介
    String introduce; // 介绍
    String cover;   //封面
    String target; //项目目标
    Date startTime;
    Date endTime;
    Double grade;
    Integer deviceId;
    Integer useGroup;
    Integer groupLimit;
    Integer hidden;
    Integer mdCourse;
    Integer useRemote;
    String fourth;
    String fourthType;
    Long emdCourse;
}
