package com.iecube.community.model.analysis.controller;

import com.iecube.community.basecontroller.analysis.AnalysisBaseController;
import com.iecube.community.model.analysis.service.AnalysisService;
import com.iecube.community.model.analysis.vo.CaseHistoryData;
import com.iecube.community.model.analysis.vo.CurrentProjectData;
import com.iecube.community.util.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/analysis")
public class AnalysisController extends AnalysisBaseController {
    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/current")
    public JsonResult<CurrentProjectData> getCurrentProjectData(Integer projectId){
        CurrentProjectData currentProjectData = analysisService.getCurrentProjectData(projectId);
        return new JsonResult<>(OK, currentProjectData);
    }

    @GetMapping("/case")
    public  JsonResult<CaseHistoryData> getCaseHistoryData(Integer projectId){
        CaseHistoryData caseHistoryData = analysisService.getCaseHistoryData(projectId);
        return new JsonResult<>(OK, caseHistoryData);
    }

}
