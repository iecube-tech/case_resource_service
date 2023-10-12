package com.iecube.community.model.analysis.dto;

import lombok.Data;

@Data
// 任务成绩平均分
public class TaskAverage {
    Integer taskNum;
    double averageGrade;
}
