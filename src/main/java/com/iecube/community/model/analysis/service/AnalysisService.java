package com.iecube.community.model.analysis.service;

import com.iecube.community.model.analysis.dto.ProjectClassHour;
import com.iecube.community.model.analysis.dto.ProjectDate;
import com.iecube.community.model.analysis.vo.*;
import com.iecube.community.util.ListCounter;

import java.util.List;

public interface AnalysisService {
    CurrentProjectData getCurrentProjectData(Integer projectId);

    CaseHistoryData getCaseHistoryData(Integer projectId);

    double currentProjectAverageGrade(Integer projectId);

//    case创建的project有多少个
    Integer projectNumByCase(Integer caseId);

//    case使用的学生有多少
    Integer studentNumByCase(Integer caseId);

//    case创建的已经结束的项目， 其最终成绩的分布直方图
    ScoreDistributionHistogram ScoreDistributionHistogramOfCase(Integer caseId);

//  case 创建额项目的每一一个任务的成绩分布直方图
    List<ScoreDistributionHistogram> ScoreDistributionHistogramOfCaseEveryTask(Integer caseId);

//    case 下的改进建议列表 计数每一个
    List<TagCountVo>  tagCounterOfCase(Integer caseId);

    //一个案例的数据统计
    CaseAnalysis getCaseAnalysis(Integer caseId);

    CaseAnalysis getProjectAnalysis(Integer projectId);

    ProjectClassHour getProjectClassHour(Integer projectId);

}

