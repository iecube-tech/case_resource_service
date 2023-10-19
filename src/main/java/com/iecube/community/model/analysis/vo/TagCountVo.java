package com.iecube.community.model.analysis.vo;

import lombok.Data;

@Data
public class TagCountVo {
    Integer id;
    String name;
    String suggestion;
    Integer taskNum;
    Integer times;
}
