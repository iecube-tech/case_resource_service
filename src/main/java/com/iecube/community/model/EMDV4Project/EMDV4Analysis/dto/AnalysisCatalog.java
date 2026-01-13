package com.iecube.community.model.EMDV4Project.EMDV4Analysis.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.Date;

@Data
public class AnalysisCatalog {
    private Integer id;
    private String projectName;
    private String semester;
    private Date startTime;
    private Date endTime;
    private String apdData;
    private JsonNode apd;
    private Integer classhour;
    private Integer stuNum;
}
