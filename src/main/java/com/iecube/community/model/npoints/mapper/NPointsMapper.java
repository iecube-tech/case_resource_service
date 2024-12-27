package com.iecube.community.model.npoints.mapper;

import com.iecube.community.model.npoints.dto.CaseModuleDto;
import com.iecube.community.model.npoints.entity.*;
import com.iecube.community.model.npoints.vo.CaseVo;
import com.iecube.community.model.npoints.vo.ConceptVo;
import com.iecube.community.model.npoints.vo.ModuleVo;
import com.iecube.community.model.npoints.entity.Module;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NPointsMapper {
    List<NPoints> getByProjectId(Integer id);

    List<Concept> getCaseByCaseId(Integer caseId);

    List<Concept> getModuleByCaseId(Integer caseId);

    List<Concept> getConceptByCaseId(Integer caseId);

    List<Link> getTargetByCaseId(Integer caseId);

    List<Link> getTargetByModuleId(Integer caseId);

    List<Case> getCasesByModuleId(Integer moduleId);

    List<Module> getModulesByConceptId(Integer conceptId);

    List<ModuleVo> getAllModules();

    List<CaseVo> getAllCases();

    List<ModuleVo> getModulesByCase(Integer caseId);

    List<ConceptVo> getConceptsByModule(Integer moduleId);

    Integer deleteCaseModule(Integer caseId, Integer moduleId);
    Integer addCaseModule(Integer caseId, Integer moduleId);



    List<CaseModuleDto> getCaseModuleListByCaseId(Integer caseId);

    Integer insertToCaseModule(CaseModuleDto caseModuleDto);

//    concept
    Integer addConcept(ConceptVo conceptVo);
    Integer delConcept(Integer Id);
    List<ConceptVo> getAllConcepts();

    Integer addConceptTemplate(ConceptVo conceptVo);
    Integer delConceptTemplate(Integer id);
    List<ConceptVo> getAllConceptTemplates();

    List<ConceptVo> getConceptTempsByModuleTempId(Integer moduleTempId);

    List<ConceptVo> getConceptTempsByModuleTemp(Integer moduleId);

//    module

    ModuleVo getModuleById(Integer moduleId);
    List<ModuleVo> getAllModuleTemplates();

    List<ModuleVo> getModuleByConceptId(Integer conceptId);

    ModuleVo getModuleTempById(Integer id);

    Integer addModuleTemplate(ModuleVo moduleVo);

    Integer delModuleTemplate(Integer moduleId);

    Integer addModule(ModuleVo moduleVo);

    Integer delModule(Integer id);

    Integer updateModule(ModuleVo moduleVo);


//    module_concept

    Integer addConceptModuleTemplate(Integer conceptId, Integer moduleId);

    Integer delModuleConceptTemplate(Integer moduleId);

    Integer addModuleConcept(Integer moduleId, Integer conceptId);

    Integer delConceptModule(Integer moduleId, Integer conceptId);


}
