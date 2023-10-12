package com.iecube.community.model.design.service;

import com.iecube.community.model.design.entity.CaseTarget;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.Design;

public interface DesignService {
    void addCaseTarget(CaseTarget caseTarget);

    void caseTargetAddKnowledgePoint(Integer targetId, String point);

    CaseDesign getCaseDesign(Integer caseId);

    void addCaseDesign(Integer caseId,Design design);

    void deleteCaseDesign(Integer caseTargetId);
}
