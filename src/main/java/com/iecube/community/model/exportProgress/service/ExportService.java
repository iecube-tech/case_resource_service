package com.iecube.community.model.exportProgress.service;

import com.iecube.community.model.exportProgress.entity.ExportProgress;

public interface ExportService {
    ExportProgress createExportTask(Integer projectId, String type);
}
