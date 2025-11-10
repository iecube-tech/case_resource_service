package com.iecube.community.model.exportProgress.service;

import com.iecube.community.model.exportProgress.entity.ExportProgress;

public interface ExportService {
    ExportProgress create(Integer projectId, String type, Integer currentUser);
    ExportProgress reCreate(Integer projectId, String type, Integer currentUser);

    ExportProgress getExportProgress(String id);

    ExportProgress cancelExportTask(String id);
}
