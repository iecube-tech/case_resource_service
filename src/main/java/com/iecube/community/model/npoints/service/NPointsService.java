package com.iecube.community.model.npoints.service;

import com.iecube.community.model.npoints.entity.NPoints;
import com.iecube.community.model.npoints.vo.*;

import java.util.List;

public interface NPointsService {
    List<NPoints> getByProjectId(Integer id);

    List getNodesByCaseId(Integer caseId);

    List getNodesByModuleId(Integer moduleId);

    List getNodesByConceptId(Integer conceptId);

    List<CaseVo> getAllCase();

    List<CaseModules> getAllCaseModules();



//    module_concept
    List<ModuleConceptVo> getAllModuleConceptTemps();

    List<ModuleConceptVo> getModulesByCase(Integer caseId);


//    concept
    void addConceptTemp(ConceptVo conceptVo);

    void delConceptTemp(Integer id);

    List<ConceptVo> getAllConceptTemps();

    void addConcept(ConceptVo conceptVo);

    List<ConceptVo> getAllConcepts();

//    module
    void addModuleConceptTemps(ModuleConceptVo moduleConceptVo, Integer user);

    void delModuleTemps(Integer moduleId, Integer user);

    void delModule(Integer moduleId);

//    case module
    List<ModuleConceptVo> moduleAddToCase(Integer moduleTempId, Integer caseId);
    List<ModuleConceptVo> moduleDelFromCase(Integer moduleId, Integer caseId);

    List<ModuleConceptVo> updateModule(ModuleConceptVo moduleConceptVo, Integer caseId);
}
