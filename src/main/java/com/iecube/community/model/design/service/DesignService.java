package com.iecube.community.model.design.service;

import com.iecube.community.model.design.entity.CaseTarget;
import com.iecube.community.model.design.qo.CourseDesignQo;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.CourseDesign;
import com.iecube.community.model.design.vo.Design;

import java.util.List;

public interface DesignService {
    void addCaseTarget(CaseTarget caseTarget);

    void caseTargetAddKnowledgePoint(Integer targetId, String point);

    CaseDesign getCaseDesign(Integer caseId);

    void addCaseDesign(Integer caseId,Design design);

    void deleteCaseDesign(Integer caseTargetId);

    List<CourseDesign> getCourseDesigns(Integer courseId);

    void addCourseDesign(Integer caseId, CourseDesignQo courseDesignQo);

    void deleteCourseDesign(Integer id);
}
