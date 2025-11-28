package com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgressData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnalysisProgressMapper {

    void createAP(AnalysisProgress ap);
    AnalysisProgress getAPById(String id);
    AnalysisProgress getApLatestByProjectId(Integer projectId);
    void updateAPById(AnalysisProgress ap);

    void createAPD(AnalysisProgressData progressData);
    AnalysisProgressData getAPD(String apId, String type);
}
