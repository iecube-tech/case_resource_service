package com.iecube.community.model.analysis.controller;

import com.iecube.community.basecontroller.analysis.AnalysisBaseController;
import com.iecube.community.model.analysis.dto.ProjectClassHour;
import com.iecube.community.model.analysis.service.AnalysisService;
import com.iecube.community.model.analysis.vo.CaseAnalysis;
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

    /**
     * 案例创建了多少project
     * 根据该案例创建的project总共有多少学生
     * case创建的已经结束的项目， 其最终成绩的分布直方图
     * case 创建额项目的每一一个任务的成绩分布直方图
     * case 下的改进建议列表 计数每一个
     */
    @GetMapping("/case_analysis")
    public JsonResult<CaseAnalysis> caseAnalysis(Integer caseId){
        CaseAnalysis caseAnalysis = analysisService.getCaseAnalysis(caseId);
        return new JsonResult<>(OK,caseAnalysis);
    }

    @GetMapping("/project_analysis")
    public JsonResult<CaseAnalysis> caseAnalysisJsonResult(Integer projectId){
        CaseAnalysis projectAnalysis = analysisService.getProjectAnalysis(projectId);
        return new JsonResult<>(OK, projectAnalysis);
    }

    @GetMapping("/project_class_hour")
    public JsonResult<ProjectClassHour> projectClassHourJsonResult(Integer projectId){
        ProjectClassHour projectClassHour = analysisService.getProjectClassHour(projectId);
        return new JsonResult<>(OK, projectClassHour);
    }

}
