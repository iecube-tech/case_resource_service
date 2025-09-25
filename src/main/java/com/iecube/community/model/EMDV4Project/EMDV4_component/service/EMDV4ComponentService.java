package com.iecube.community.model.EMDV4Project.EMDV4_component.service;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;

public interface EMDV4ComponentService {
    EMDV4Component updateStatus(String id, int status);
    EMDV4Component updatePayload(String id, String payload);
    EMDV4Component updateScore(String id, double score);
    EMDV4Component checkScore(String id, double score);

    void batchUpdateStatus(EMDV4Component component);
    void batchUpdatePayload(EMDV4Component component);
    void batchUpdateAiScore(EMDV4Component component);
    void batchUpdateTScore(EMDV4Component component);
}
