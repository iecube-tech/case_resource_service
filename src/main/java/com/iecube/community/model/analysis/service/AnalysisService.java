package com.iecube.community.model.analysis.service;

import com.iecube.community.model.analysis.dto.ProjectDate;
import com.iecube.community.model.analysis.vo.CaseHistoryData;
import com.iecube.community.model.analysis.vo.CurrentProjectData;

public interface AnalysisService {
    CurrentProjectData getCurrentProjectData(Integer projectId);

    CaseHistoryData getCaseHistoryData(Integer projectId);

    double currentProjectAverageGrade(Integer projectId);


}

