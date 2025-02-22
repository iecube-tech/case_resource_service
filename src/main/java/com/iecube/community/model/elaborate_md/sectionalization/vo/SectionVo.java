package com.iecube.community.model.elaborate_md.sectionalization.vo;

import com.iecube.community.model.elaborate_md.block.entity.Block;
import lombok.Data;

import java.util.List;

@Data
public class SectionVo {
    private long id;
    private long parentId; //LabProcId
    private int sort;
    private boolean hasChildren;
    private String name="分节";
    private int level=2;
    private List<Block> BlockList;
}
