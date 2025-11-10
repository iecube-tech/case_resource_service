package com.iecube.community.model.exportProgress.service;

import com.iecube.community.model.exportProgress.dto.PstReportDTO;

import java.util.List;

public interface ExportChildService {

    void processReportExportTask(String progressId, int projectId, List<PstReportDTO> pstReportDTOList, Integer currentUser);

    void processGradeExportTask(String progressId, Integer projectId);
}
