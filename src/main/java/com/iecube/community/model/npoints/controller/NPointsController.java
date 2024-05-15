package com.iecube.community.model.npoints.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.npoints.entity.NPoints;
import com.iecube.community.model.npoints.service.NPointsService;
import com.iecube.community.model.npoints.vo.*;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.swing.plaf.PanelUI;
import java.util.List;

@RestController
@RequestMapping("/points")
public class NPointsController extends BaseController {
    @Autowired
    private NPointsService nPointsService;
    public static final int OK=200;

    @GetMapping("/p")
    public JsonResult<List> getByProjectId( Integer id){
        List<NPoints> PointsCorrelations = nPointsService.getByProjectId(id);
        return new JsonResult<List>(OK, PointsCorrelations);
    }

    @GetMapping("/nodes_by_case")
    public JsonResult<List> getNodesByCaseId(Integer caseId){
        List Nodes = nPointsService.getNodesByCaseId(caseId);
        return new JsonResult<>(OK, Nodes);
    }

    @GetMapping("/nodes_by_module")
    public JsonResult<List> getNodesByModuleId(Integer moduleId){
        List Nodes = nPointsService.getNodesByModuleId(moduleId);
        return new JsonResult<>(OK, Nodes);
    }

    @GetMapping("/nodes_by_concept")
    public JsonResult<List> getNodesByConceptId(Integer conceptId){
        List Nodes = nPointsService.getNodesByConceptId(conceptId);
        return new JsonResult<>(OK, Nodes);
    }



    @GetMapping("/all_cases")
    public JsonResult<List> getAllCases(){
        List<CaseVo> allCases = nPointsService.getAllCase();
        return new JsonResult<>(OK,allCases);
    }

    @PostMapping("/add_concept")
    public JsonResult<List> addConcept(@RequestBody ConceptVo conceptVo){
        nPointsService.addConcept(conceptVo);
        List<ConceptVo> allConcepts = nPointsService.getAllConcepts();
        return new JsonResult<>(OK, allConcepts);
    }



    @GetMapping("/all_concept")
    public JsonResult<List> AllConcepts(){
        List<ConceptVo> allConcepts = nPointsService.getAllConcepts();
        return new JsonResult<>(OK, allConcepts);
    }

//    concept 操作
    @GetMapping("/all_concept_tem")
    public JsonResult<List> getAllConcepts(){
        List<ConceptVo> allConcepts = nPointsService.getAllConceptTemps();
        return new JsonResult<>(OK,allConcepts);
    }

    @PostMapping("/add_concept_tem")
    public JsonResult<List> addConceptTemps(@RequestBody ConceptVo conceptVo){
        nPointsService.addConceptTemp(conceptVo);
        List<ConceptVo> allConcepts = nPointsService.getAllConceptTemps();
        return new JsonResult<>(OK, allConcepts);
    }

    @DeleteMapping("/del_concept_tem")
    public JsonResult<List> delConceptTemp(Integer id){
        nPointsService.delConceptTemp(id);
        List<ConceptVo> allConcepts = nPointsService.getAllConceptTemps();
        return new JsonResult<>(OK, allConcepts);
    }

//    module_concept操作  module操作
    @GetMapping("/all_module_tem")
    public JsonResult<List> getAllModuleConceptTemp(){
        List<ModuleConceptVo> moduleConceptVoList = nPointsService.getAllModuleConceptTemps();
        return new JsonResult<>(OK,moduleConceptVoList);
    }

    @PostMapping("/add_module_tem")
    public JsonResult<List> addModule(@RequestBody ModuleConceptVo moduleConceptVo, HttpSession session){
        Integer user = getUserIdFromSession(session);
        nPointsService.addModuleConceptTemps(moduleConceptVo, user);
        List<ModuleConceptVo> moduleConceptVos = nPointsService.getAllModuleConceptTemps();
        return new JsonResult<>(OK, moduleConceptVos);
    }

    @DeleteMapping("/del_module_tem")
    public JsonResult<List> delModuleTemp(Integer id, HttpSession session){
        Integer user  = getUserIdFromSession(session);
        nPointsService.delModuleTemps(id,user);
        List<ModuleConceptVo> moduleConceptVos = nPointsService.getAllModuleConceptTemps();
        return new JsonResult<>(OK, moduleConceptVos);
    }


    @GetMapping("/modules_of_case/{caseId}")
    public JsonResult<List> modulesOfCase(@PathVariable Integer caseId){
        List<ModuleConceptVo> moduleConceptVos = nPointsService.getModulesByCase(caseId);
        return new JsonResult<>(OK,moduleConceptVos);
    }

    @PostMapping("/case_add_module")
    public JsonResult<List> caseAddModule(Integer caseId, Integer moduleTempId){
        List<ModuleConceptVo> moduleConceptVoList = nPointsService.moduleAddToCase(moduleTempId,caseId);
        return new JsonResult<>(OK, moduleConceptVoList);
    }

    @DeleteMapping("/case_del_module")
    public JsonResult<List> caseDelModule(Integer caseId, Integer moduleId){
        List<ModuleConceptVo> moduleConceptVoList = nPointsService.moduleDelFromCase(moduleId,caseId);
        return new JsonResult<>(OK, moduleConceptVoList);
    }

    @PostMapping("/module_update")
    public JsonResult<List> updateModule(@RequestBody ModuleConceptVo moduleConceptVo, Integer caseId){
        List<ModuleConceptVo> res = nPointsService.updateModule(moduleConceptVo, caseId);
        return new JsonResult<>(OK, res);
    }

}
