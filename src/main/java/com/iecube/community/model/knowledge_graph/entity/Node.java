package com.iecube.community.model.knowledge_graph.entity;

import lombok.Data;

import java.util.List;

@Data
public class Node {
    Integer id;
    Integer pId;
    Integer level;
    String name;
    String itemColor;
    String labelColor;
    String labelPosition;
    Integer labelFontSize;
    Integer symbolSize;
    String link;
    List<Node> children;
}
