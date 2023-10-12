package com.iecube.community.model.design.vo;

import lombok.Data;

import java.util.List;

@Data
public class CaseDesign {
    Integer caseId;
    List<Design> designs;
}
