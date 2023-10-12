package com.iecube.community.model.analysis.dto;

import lombok.Data;

@Data
//该项目的人员分布在哪些任务
public class PersonnelDistribution {
    Integer taskNum;
    Integer studentNum;  //数量
}
