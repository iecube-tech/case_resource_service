package com.iecube.community.model.analysis.dto;

import lombok.Data;

@Data
//任务成绩中位数
public class TaskMedian {
    Integer taskNum;
    double medianGrade;
}
