package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;

public interface AnalysisDataGenService {
     void dataGen(Integer projectId, AnalysisProgress progress);
     void dataTest(Integer projectId, AnalysisProgress progress);
}
