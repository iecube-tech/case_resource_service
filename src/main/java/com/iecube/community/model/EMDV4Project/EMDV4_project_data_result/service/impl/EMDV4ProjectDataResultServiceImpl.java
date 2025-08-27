package com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.entity.EMDV4ProjectDataResult;
import com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.mapper.EMDV4ProjectDataResultMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_project_data_result.service.EMDV4ProjectDataResultService;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EMDV4ProjectDataResultServiceImpl implements EMDV4ProjectDataResultService {

    @Autowired
    private EMDV4ProjectDataResultMapper emdV4ProjectDataResultMapper;


    @Override
    public void createEMDV4ProjectDataResultService(Integer projectId) {
        EMDV4ProjectDataResult record = new EMDV4ProjectDataResult();
        record.setProjectId(projectId);
        record.setProgress(0);
        record.setTaskCompletionRate(0.0);
        record.setTagAchievementRate(0.0);
        record.setAverageScore(0.0);
        record.setPassRate(0.0);
        record.setAiUseRate(0.0);
        record.setDegreeOfAchievementOfCourseTarget(0.0);
        int res = emdV4ProjectDataResultMapper.insert(record);
        if(res!=1){
            throw new InsertException("新增课程分析工具异常");
        }
    }
}
