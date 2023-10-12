package com.iecube.community.model.design.controller;

import com.iecube.community.basecontroller.design.DesignBaseController;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.Design;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.security.krb5.internal.crypto.Des;

import java.util.List;

@RestController
@RequestMapping("/design")
public class DesignController extends DesignBaseController {
    @Autowired
    private DesignService designService;

    @PostMapping("/add_design/{caseId}")
    public JsonResult<CaseDesign> contentAddDesign(@PathVariable Integer caseId, @RequestBody Design design){
        designService.addCaseDesign(caseId,design);
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK,caseDesign);
    }

    @GetMapping("/{caseId}")
    public JsonResult<CaseDesign> getCaseDesigns(@PathVariable Integer caseId){
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK,caseDesign);
    }

    @PostMapping("/delete/{caseId}")
    public JsonResult<CaseDesign> deleteCaseDesign(@PathVariable Integer caseId, Integer caseTargetId){
        designService.deleteCaseDesign(caseTargetId);
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK, caseDesign);
    }
}
