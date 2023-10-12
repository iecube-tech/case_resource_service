package com.iecube.community.model.npoints.vo;

import lombok.Data;

import java.util.List;

@Data
public class CaseModules {
    Integer id;
    String name;
    List<ModuleConceptVo> children;
}
