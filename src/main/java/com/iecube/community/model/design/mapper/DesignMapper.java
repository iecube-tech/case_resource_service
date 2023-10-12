package com.iecube.community.model.design.mapper;

import com.iecube.community.model.design.entity.CaseTarget;
import com.iecube.community.model.design.entity.KnowledgePoint;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DesignMapper {
    Integer addCaseTarget(CaseTarget caseTarget);
    Integer deleteCaseTarget(Integer caseTargetId);
    Integer addKnowledgePoint(KnowledgePoint knowledgePoint);
    Integer deleteKnowledgePoint(Integer knowledgePointId);
    List<CaseTarget> getCaseTargetsByCase(Integer caseId);
    List<KnowledgePoint> getKnowledgePointsByTargetId(Integer targetId);
}
