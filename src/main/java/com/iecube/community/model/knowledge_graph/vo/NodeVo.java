package com.iecube.community.model.knowledge_graph.vo;

import com.iecube.community.model.knowledge_graph.dto.ItemStyle;
import com.iecube.community.model.knowledge_graph.dto.Label;
import lombok.Data;

import java.util.List;

@Data
public class NodeVo {
    Integer id;
    Integer pId;
    Integer level;
    String name;
    ItemStyle itemStyle;
    Label label;
    Integer symbolSize;
    String link;
    List<NodeVo> children;
}
