package com.iecube.community.model.npoints.mapper;

import com.iecube.community.model.npoints.dto.CaseModuleDto;
import com.iecube.community.model.npoints.entity.*;
import com.iecube.community.model.npoints.vo.CaseVo;
import com.iecube.community.model.npoints.vo.ConceptVo;
import com.iecube.community.model.npoints.vo.ModuleConceptVo;
import com.iecube.community.model.npoints.vo.ModuleVo;
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

    List<ConceptVo> getAllConcepts();

    List<ModuleVo> getAllModules();

    List<CaseVo> getAllCases();

    List<ModuleVo> getModulesByCase(Integer caseId);

    List<ConceptVo> getConceptsByModule(Integer moduleId);

    Integer deleteCaseModule(Integer caseId, Integer moduleId);
    Integer addCaseModule(Integer caseId, Integer moduleId);

    Integer addConcept(ConceptVo conceptVo);

    Integer addModule(ModuleVo moduleVo);

    Integer addModuleConcept(Integer moduleId, Integer conceptId);

    List<CaseModuleDto> getCaseModuleListByCaseId(Integer caseId);

    Integer insertToCaseModule(CaseModuleDto caseModuleDto);
}
