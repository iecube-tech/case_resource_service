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

    @GetMapping("/all_concepts")
    public JsonResult<List> getAllConcepts(){
        List<ConceptVo> allConcepts = nPointsService.getAllConcepts();
        return new JsonResult<>(OK,allConcepts);
    }

    @GetMapping("/all_modules")
    public JsonResult<List> getAllModules(){
        List<ModuleVo> allModules = nPointsService.getAllModules();
        return new JsonResult<>(OK,allModules);
    }

    @GetMapping("/all_cases")
    public JsonResult<List> getAllCases(){
        List<CaseVo> allCases = nPointsService.getAllCase();
        return new JsonResult<>(OK,allCases);
    }

    @GetMapping("/case_tree")
    public JsonResult<List> getCaseTree(){
        List<CaseModules> caseModules = nPointsService.getAllCaseModules();
        return new JsonResult<>(OK, caseModules);
    }

    @GetMapping("/module_tree")
    public JsonResult<List> getModuleTree(){
        List<ModuleConceptVo> moduleConceptVoList = nPointsService.getAllModuleConcepts();
        return new JsonResult<>(OK,moduleConceptVoList);
    }

    @GetMapping("/modules_of_case/{caseId}")
    public JsonResult<List> modulesOfCase(@PathVariable Integer caseId){
        List<ModuleConceptVo> moduleConceptVos = nPointsService.getModulesByCase(caseId);
        return new JsonResult<>(OK,moduleConceptVos);
    }

    @PostMapping("/add_concept")
    public JsonResult<List> addConcept(@RequestBody ConceptVo conceptVo){
        System.out.println(conceptVo);
        nPointsService.addConcept(conceptVo);
        List<ConceptVo> allConcepts = nPointsService.getAllConcepts();
        return new JsonResult<>(OK, allConcepts);
    }


}
