package com.iecube.community.model.EMDV4.LabComponent.vo;

import com.iecube.community.model.EMDV4.Tag.vo.BLTTagVo;
import lombok.Data;

@Data
public class LabComponentVo {
    private Long id;
    private Long tag;
    private String name;
    private String icon;
    private String type;
    private Boolean needCalculate;
    private Double totalScore;
    private Double score;
    private Double scoreProportion;
    private String style;
    private String config;
    private String payload;
    private BLTTagVo componentTag;
}
