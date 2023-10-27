package com.iecube.community.model.npoints.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.npoints.entity.*;
import com.iecube.community.model.npoints.mapper.NPointsMapper;
import com.iecube.community.model.npoints.service.NPointsService;
import com.iecube.community.model.npoints.vo.*;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NPointsServiceImpl implements NPointsService {
    @Autowired
    private NPointsMapper nPointsMapper;

    private Integer diameter=120; //圆的直径
    private Integer interval=50; //间距
    private Integer intervalTwo=80;
    private Integer center=700; // 画布横向中心位置
    private Integer rowOne=90; // 第一行的坐标
    private Integer rowTwo = (int)Math.floor((diameter / 2) * 3.5 + rowOne);  // 第二行的坐标
    private Integer rowThree = (int)Math.floor(rowTwo+(3.5*120)/2);  // 第三行的坐标

    @Override
    public List<NPoints> getByProjectId(Integer id) {
        List<NPoints> PointsCorrelations = nPointsMapper.getByProjectId(id);
        return PointsCorrelations;
    }

    @Override
    public List getNodesByCaseId(Integer caseId) {
        List<Concept> caseNode = nPointsMapper.getCaseByCaseId(caseId);
        List<Link> caseNodeTarget = nPointsMapper.getTargetByCaseId(caseId);
        List<Concept> moduleNodes = nPointsMapper.getModuleByCaseId(caseId);
        List<Concept> conceptNodes = nPointsMapper.getConceptByCaseId(caseId);
        List Nodes = new ArrayList<>();
        List Links = new ArrayList<>();
        for (int a=0; a<caseNode.size(); a++){
            Concept caseA = caseNode.get(a);
            caseA.setY(rowOne);
            caseA.setX(center);
            ItemStyle lineOneStyle= new ItemStyle();
            Emphasis lineOneEmphasis = new Emphasis();
            ItemStyle lineOneEmphasisStyle = new ItemStyle();
            lineOneStyle.setColor("#fff");
            lineOneStyle.setBorderColor("#33b8b9");
            lineOneStyle.setBorderWidth(4);
            lineOneEmphasisStyle.setColor("#33b8b9");
            lineOneEmphasis.setItemStyle(lineOneEmphasisStyle);
            caseA.setItemStyle(lineOneStyle);
            caseA.setEmphasis(lineOneEmphasis);
            Nodes.add(caseNode.get(a));
        }
        for(Link link : caseNodeTarget){
            link.setSource(caseNode.get(0).getName());
            Links.add(link);
        }
//        System.out.println("moduleNodes.size():"+moduleNodes.size());
//        System.out.println((moduleNodes.size()/2-0.5)*150);
        // 确定第一个圆的位置
        Integer rowTwoFirst = center - (int)((moduleNodes.size()/2-0.5)*(diameter+intervalTwo));
        Integer rowThreeFirst = center - (int)Math.floor( (conceptNodes.size()/2-0.5)*(diameter+interval) );
        if(moduleNodes.size()%2 == 1){
//            System.out.println("rowTwoFirst"+"奇数");
            rowTwoFirst = center - (int)Math.floor(moduleNodes.size()/2*(diameter+intervalTwo));
        }
        if(conceptNodes.size()%2 ==1){
//            System.out.println("rowTwoThree"+"奇数");
            rowThreeFirst =  center - (int)Math.floor(conceptNodes.size()/2*(diameter+interval));
        }
//        System.out.println("rowTwoFirst"+rowTwoFirst);
        // 模块  第二行的圆
        for (int b=0; b<moduleNodes.size(); b++){
            Concept module = moduleNodes.get(b);
            module.setY(rowTwo);
            module.setX(rowTwoFirst + b * (diameter+intervalTwo) );
            ItemStyle lineTwoStyle = new ItemStyle();
            lineTwoStyle.setColor("#fff");
            lineTwoStyle.setBorderWidth(4);
            lineTwoStyle.setBorderColor("#97f7db");
            Emphasis lineTwoEmphasis = new Emphasis();
            ItemStyle lineTwoEmphasisStyle = new ItemStyle();
            lineTwoEmphasisStyle.setColor("#97f7db");
            lineTwoEmphasis.setItemStyle(lineTwoEmphasisStyle);
            module.setItemStyle(lineTwoStyle);
            module.setEmphasis(lineTwoEmphasis);
            Nodes.add(moduleNodes.get(b));
            List<Link> moduleNodeTargets = nPointsMapper.getTargetByModuleId(moduleNodes.get(b).getModuleId());
            for (Link link : moduleNodeTargets){
                link.setSource(moduleNodes.get(b).getName());
                Links.add(link);
            }
        }
//        System.out.println("rowThreeFirst"+rowThreeFirst);
//        System.out.println(Links);
        // 基础概念 第三行的圆
        for (int c=0; c<conceptNodes.size(); c++){
            Concept concept = conceptNodes.get(c);
            ItemStyle lineThreeStyle = new ItemStyle();
            lineThreeStyle.setColor("#fff");
            lineThreeStyle.setBorderWidth(4);
            lineThreeStyle.setBorderColor("#bfc9fd");
            Emphasis lineThreeEmphasis = new Emphasis();
            ItemStyle lineThreeEmphasisStyle = new ItemStyle();
            lineThreeEmphasisStyle.setColor("#bfc9fd");
            lineThreeEmphasis.setItemStyle(lineThreeEmphasisStyle);
            concept.setItemStyle(lineThreeStyle);
            concept.setEmphasis(lineThreeEmphasis);
            concept.setY(rowThree);
            concept.setX(rowThreeFirst + c * (diameter+interval));
            Label label = new Label();
            label.setOverflow("break");
            label.setWidth(110);
            concept.setLabel(label);
            Nodes.add(conceptNodes.get(c));
        }
//        System.out.println(Nodes);
        List<List> result = new ArrayList<>();
        result.add(Nodes);
        result.add(Links);
        List<List> lastResult = new ArrayList<>();
        lastResult.add(result);
        return lastResult;
    }

    @Override
    public List getNodesByModuleId(Integer moduleId) {
        List<Case> cases = nPointsMapper.getCasesByModuleId(moduleId);
        List allNodes = new ArrayList<>();
        for (Case cas : cases){
            List caseNodes = getNodesByCaseId(cas.getId());
            for(int i=0; i<caseNodes.size(); i++){
                allNodes.add(caseNodes.get(i));
            }
        }
        return allNodes;
    }

    @Override
    public List getNodesByConceptId(Integer conceptId) {
        List<Module> modules = nPointsMapper.getModulesByConceptId(conceptId);
        List allNodes = new ArrayList<>();
        for ( Module module : modules){
            List NodesByModule = getNodesByModuleId(module.getId());
            for(int i=0; i < NodesByModule.size(); i++){
                allNodes.add(NodesByModule.get(i));
            }
        }
        return allNodes;
    }

    @Override
    public List<ConceptVo> getAllConcepts() {
        List<ConceptVo> allConcepts = nPointsMapper.getAllConcepts();
        return allConcepts;
    }

    @Override
    public List<ModuleVo> getAllModules() {
        List<ModuleVo> allModules = nPointsMapper.getAllModules();
        return allModules;
    }

    @Override
    public List<CaseVo> getAllCase() {
        List<CaseVo> allCases = nPointsMapper.getAllCases();
        return allCases;
    }

    @Override
    public List<CaseModules> getAllCaseModules() {
        List<CaseVo> cases = nPointsMapper.getAllCases();
        List<CaseModules> caseModulesList = new ArrayList<>();
        for(CaseVo c : cases){
            CaseModules caseModules = new CaseModules();
            caseModules.setId(c.getId());
            caseModules.setName(c.getName());
            List<ModuleVo> moduleVos = nPointsMapper.getModulesByCase(c.getId());
            List<ModuleConceptVo> moduleConceptVos = new ArrayList<>();
            for(ModuleVo moduleVo : moduleVos){
                ModuleConceptVo moduleConceptVo = new ModuleConceptVo();
                moduleConceptVo.setId(moduleVo.getId());
                moduleConceptVo.setName(moduleVo.getName());
                List<ConceptVo> conceptVos = nPointsMapper.getConceptsByModule(moduleVo.getId());
                moduleConceptVo.setChildren(conceptVos);
                moduleConceptVos.add(moduleConceptVo);
            }
            caseModules.setChildren(moduleConceptVos);
            caseModulesList.add(caseModules);
        }
        return caseModulesList;
    }

    @Override
    public List<ModuleConceptVo> getAllModuleConcepts() {
        List<ModuleVo> moduleVos = nPointsMapper.getAllModules();
        List<ModuleConceptVo> moduleConceptVoList = new ArrayList<>();
        for(ModuleVo m : moduleVos){
            ModuleConceptVo moduleConceptVo = new ModuleConceptVo();
            moduleConceptVo.setId(m.getId());
            moduleConceptVo.setName(m.getName());
            List<ConceptVo> conceptVos = nPointsMapper.getConceptsByModule(m.getId());
            moduleConceptVo.setChildren(conceptVos);
            moduleConceptVoList.add(moduleConceptVo);
        }
        return moduleConceptVoList;
    }

    @Override
    public List<ModuleConceptVo> getModulesByCase(Integer caseId) {
        List<ModuleVo> moduleVos = nPointsMapper.getModulesByCase(caseId);
        List<ModuleConceptVo> moduleConceptVos = new ArrayList<>();
        for(ModuleVo moduleVo : moduleVos){
            ModuleConceptVo moduleConceptVo = new ModuleConceptVo();
            moduleConceptVo.setId(moduleVo.getId());
            moduleConceptVo.setName(moduleVo.getName());
            List<ConceptVo> conceptVos = nPointsMapper.getConceptsByModule(moduleVo.getId());
            moduleConceptVo.setChildren(conceptVos);
            moduleConceptVos.add(moduleConceptVo);
        }
        return moduleConceptVos;
    }

    @Override
    public void addConcept(ConceptVo conceptVo) {
        Integer row = nPointsMapper.addConcept(conceptVo);
        if(row!=1){
            throw new InsertException("插入数据异常");
        }
    }
}
