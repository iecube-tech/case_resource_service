package com.iecube.community.model.knowledge_graph.qo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iecube.community.model.knowledge_graph.dto.ItemStyle;
import com.iecube.community.model.knowledge_graph.dto.Label;
import lombok.Data;

@Data
public class NodeQo {
    @JsonProperty(value = "id")
    Integer id;
    @JsonProperty(value = "pid")
    Integer pId;
    Integer level;
    String name;
    ItemStyle itemStyle;
    Label label;
    Integer symbolSize;
    String link;
}
