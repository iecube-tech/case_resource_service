package com.iecube.community.model.EMDV4Project.EMDV4Analysis.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgressData;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.vo.AnalysisInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnalysisProgressMapper {

    void createAP(AnalysisProgress ap);
    AnalysisProgress getAPById(String id);
    AnalysisProgress getApLatestByProjectId(Integer projectId);
    AnalysisProgress getApLatestSuccessByProjectId(Integer projectId);
    void updateAPById(AnalysisProgress ap);

    void createAPD(AnalysisProgressData progressData);
    void batchCreatAPD(List<AnalysisProgressData> list);
    AnalysisProgressData getAPD(String apId, String type);
    AnalysisProgressData getAPDWithPtId(String apId, String type, Long ptId);
    AnalysisProgressData getAPDWithStudentId(String apId, String type, String studentId);
    AnalysisProgressData getAPDWithPtIdAndPsId(String apId, String type, Long ptId, Long psId);

    AnalysisInfo getAnalysisInfo(Integer projectId);

    List<Integer> getEmdv4ProjectIdInTime();

}
