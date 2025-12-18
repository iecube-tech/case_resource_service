package com.iecube.community.model.EMDV4Project.EMDV4Analysis.service;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.entity.AnalysisProgress;

import java.util.concurrent.CompletableFuture;

public interface AnalysisDataGenService {
     CompletableFuture<Void> dataGen(Integer projectId, AnalysisProgress progress);
     void dataTest(Integer projectId, AnalysisProgress progress);
}
