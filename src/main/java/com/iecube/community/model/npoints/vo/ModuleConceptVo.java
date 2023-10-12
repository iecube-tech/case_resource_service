package com.iecube.community.model.npoints.vo;

import lombok.Data;

import java.util.List;

@Data
public class ModuleConceptVo {
    Integer id;
    String name;
    List<ConceptVo> children;
}
