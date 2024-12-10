package com.iecube.community.model.knowledge_graph.mapper;

import com.iecube.community.model.knowledge_graph.entity.CaseNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseNodeMapper {
    Integer insertCaseNode(CaseNode caseNode);

    CaseNode getCaseNode(Integer caseId, Integer nodeId);

    List<CaseNode> getListByCaseId(Integer caseId);

    CaseNode getByCaseId(Integer caseId);

    CaseNode getByNodeId(Integer nodeId);

    int deleteByCaseId(Integer caseId);
}
