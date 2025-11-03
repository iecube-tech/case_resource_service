package com.iecube.community.model.elaborate_md.lab_model.vo;

import com.iecube.community.model.elaborate_md.sectionalization.vo.SectionVo;
import lombok.Data;

import java.util.List;

@Data
public class LabModelVo {
    private Long id;
    private Long parentId;
    private String name;
    private String icon;
    private Integer sort;
    private String stage;
    private int level=2;
    private boolean hasChildren;
    private List<SectionVo> sectionVoList;
}
