package com.iecube.community.model.knowledge_graph.mapper;

import com.iecube.community.model.knowledge_graph.entity.CaseNode;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaseNodeMapper {
    Integer insertCaseNode(CaseNode caseNode);

    CaseNode getByCaseId(Integer caseId);
}
