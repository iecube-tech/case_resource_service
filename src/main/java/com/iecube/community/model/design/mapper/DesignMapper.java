package com.iecube.community.model.design.mapper;

import com.iecube.community.model.design.entity.*;
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

    List<GraduationRequirement> getGraduationRequirementListByCourseId(Integer courseId);

    List<GraduationPoint> getGraduationPointList(Integer parentId);
    List<CourseTarget> getCourseTargetList(Integer parentId);
    List<CourseChapter> getCourseChapterList(Integer parentId);
}
