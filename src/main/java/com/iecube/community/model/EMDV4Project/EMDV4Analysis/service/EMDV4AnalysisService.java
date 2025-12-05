package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;
import org.json.JSONObject;

public interface EMDV4AnalysisService {
    AnalysisProgress createGenProgress(Integer projectId);

    AnalysisProgress createGenProgressTest(Integer projectId);

    JsonNode getData(Integer projectId, String type);

    JsonNode getTaskData(Integer projectId, String type, Long ptId);

    JsonNode getStuData(Integer projectId, String type, String studentId);

    JsonNode getPSTData(Integer projectId, String type, Long ptId, Long psId);
}
