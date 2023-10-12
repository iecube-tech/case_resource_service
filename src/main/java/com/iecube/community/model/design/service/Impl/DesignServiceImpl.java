package com.iecube.community.model.design.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.design.entity.CaseTarget;
import com.iecube.community.model.design.entity.KnowledgePoint;
import com.iecube.community.model.design.mapper.DesignMapper;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.Design;
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
        System.out.println("targetId:"+targetId);
        for (KnowledgePoint knowledgePoint : design.getKnowledgePoints()){
            knowledgePoint.setTargetId(targetId);
            System.out.println(knowledgePoint);
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
}
