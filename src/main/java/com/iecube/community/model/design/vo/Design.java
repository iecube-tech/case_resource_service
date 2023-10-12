package com.iecube.community.model.design.vo;

import com.iecube.community.model.design.entity.KnowledgePoint;
import lombok.Data;

import java.util.List;

@Data
public class Design {
    Integer id;  // targetId
    String targetName;
    List<KnowledgePoint> knowledgePoints;
}
