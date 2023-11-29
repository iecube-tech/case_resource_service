package com.iecube.community.model.analysis.dto;

import lombok.Data;

@Data
public class ProjectClassHour {
    Double classHour; // 项目课时
    Double totalClassHour; //所有学生课时之和
    Double completedClassHour; // 已提交的总课时
    Double completedPercent; // 已提交占比
    Double redaOVerClassHour; // 已批阅的总课时
    Double redaOverPercent;  // 已批阅占比
}