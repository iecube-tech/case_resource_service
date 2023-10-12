package com.iecube.community.model.npoints.service;

import com.iecube.community.model.npoints.entity.NPoints;
import com.iecube.community.model.npoints.vo.*;

import java.util.List;

public interface NPointsService {
    List<NPoints> getByProjectId(Integer id);

    List getNodesByCaseId(Integer caseId);

    List getNodesByModuleId(Integer moduleId);

    List getNodesByConceptId(Integer conceptId);

    List<ConceptVo> getAllConcepts();

    List<ModuleVo> getAllModules();

    List<CaseVo> getAllCase();

    List<CaseModules> getAllCaseModules();

    List<ModuleConceptVo> getAllModuleConcepts();

    List<ModuleConceptVo> getModulesByCase(Integer caseId);
}
