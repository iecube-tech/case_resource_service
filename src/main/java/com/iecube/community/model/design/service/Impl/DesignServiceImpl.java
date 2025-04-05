package com.iecube.community.model.design.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.design.entity.*;
import com.iecube.community.model.design.mapper.DesignMapper;
import com.iecube.community.model.design.qo.CourseDesignQo;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.CourseDesign;
import com.iecube.community.model.design.vo.Design;
import com.iecube.community.model.direction.service.ex.DeleteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DesignServiceImpl implements DesignService {

    @Autowired
    private DesignMapper designMapper;

    @Override
    public void addCaseTarget(CaseTarget caseTarget) {
        Integer row = designMapper.addCaseTarget(caseTarget);
        if(row!=1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public void caseTargetAddKnowledgePoint(Integer targetId, String point) {
        KnowledgePoint knowledgePoint = new KnowledgePoint();
        knowledgePoint.setTargetId(targetId);
        knowledgePoint.setPoint(point);
        Integer row = designMapper.addKnowledgePoint(knowledgePoint);
        if(row!=1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public CaseDesign getCaseDesign(Integer caseId) {
        CaseDesign caseDesign = new CaseDesign();
        List<CaseTarget> caseTargetList = designMapper.getCaseTargetsByCase(caseId);
        List<Design> designList = new ArrayList<>();
        for(CaseTarget caseTarget:caseTargetList){
            Design design = new Design();
            design.setId(caseTarget.getId());
            design.setTargetName(caseTarget.getTargetName());
            List<KnowledgePoint> knowledgePoints = designMapper.getKnowledgePointsByTargetId(caseTarget.getId());
            design.setKnowledgePoints(knowledgePoints);
            designList.add(design);
        }
        caseDesign.setCaseId(caseId);
        caseDesign.setDesigns(designList);
        return caseDesign;
    }

    @Override
    public void addCaseDesign(Integer caseId ,Design design) {
        CaseTarget caseTarget = new CaseTarget();
        caseTarget.setTargetName(design.getTargetName());
        caseTarget.setCaseId(caseId);
        Integer row = designMapper.addCaseTarget(caseTarget);
        if(row!=1){
            throw new InsertException("插入数据异常");
        }
        Integer targetId = caseTarget.getId();
//        System.out.println("targetId:"+targetId);
        for (KnowledgePoint knowledgePoint : design.getKnowledgePoints()){
            knowledgePoint.setTargetId(targetId);
//            System.out.println(knowledgePoint);
            designMapper.addKnowledgePoint(knowledgePoint);
        }
    }

    @Override
    public void deleteCaseDesign(Integer caseTargetId) {
        //知识点的 target_id 是 caseTargetId 的所有条目 删除
        List<KnowledgePoint> knowledgePointList = designMapper.getKnowledgePointsByTargetId(caseTargetId);
        for(KnowledgePoint knowledgePoint:knowledgePointList){
            designMapper.deleteKnowledgePoint(knowledgePoint.getId());
        }
        designMapper.deleteCaseTarget(caseTargetId);
    }

    @Override
    public List<CourseDesign> getCourseDesigns(Integer courseId) {
        List<GraduationRequirement> graduationRequirementList = designMapper.getGraduationRequirementListByCourseId(courseId);
        List<CourseDesign> courseDesignList = new ArrayList<>();
        for(GraduationRequirement graduationRequirement : graduationRequirementList){
            List<GraduationPoint> graduationPointList = designMapper.getGraduationPointList(graduationRequirement.getId());
            List<CourseTarget> courseTargetList = designMapper.getCourseTargetList(graduationRequirement.getId());
//            List<CourseChapter> courseChapterList = designMapper.getCourseChapterList(graduationRequirement.getId());
            CourseDesign courseDesign = new CourseDesign();
            courseDesign.setGraduationRequirementId(graduationRequirement.getId());
            courseDesign.setGraduationRequirementName(graduationRequirement.getName());
            courseDesign.setGraduationPointList(graduationPointList);
            courseDesign.setCourseTargetList(courseTargetList);
//            courseDesign.setCourseChapterList(courseChapterList);
            courseDesignList.add(courseDesign);
        }
        return courseDesignList;
    }

    @Override
    public void addCourseDesign(Integer caseId, CourseDesignQo courseDesignQo) {
        GraduationRequirement graduationRequirement = new GraduationRequirement();
        graduationRequirement.setCourseId(caseId);
        graduationRequirement.setName(courseDesignQo.getGraduationRequirementName());
        designMapper.addGraduationRequirement(graduationRequirement);
        Integer parentId = graduationRequirement.getId();
        for(GraduationPoint graduationPoint : courseDesignQo.getGraduationPointList()){
            graduationPoint.setParentId(parentId);
            designMapper.addGraduationPoint(graduationPoint);
        }
        for(CourseTarget courseTarget: courseDesignQo.getCourseTargetList()){
            courseTarget.setParentId(parentId);
            designMapper.addCourseTarget(courseTarget);
        }
//        for(CourseChapter courseChapter: courseDesignQo.getCourseChapterList()){
//            courseChapter.setParentId(parentId);
//            designMapper.addCourseChapter(courseChapter);
//        }
    }

    @Override
    public void deleteCourseDesign(Integer id) {
        if(id == null){
            throw new DeleteException("缺少参数");
        }
        designMapper.deleteGraduationRequirement(id);
        designMapper.deleteGraduationPoint(id);
        designMapper.deleteCourseTarget(id);
        designMapper.deleteCourseChapter(id);
    }
}
