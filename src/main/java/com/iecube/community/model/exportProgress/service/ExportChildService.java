package com.iecube.community.model.exportProgress.service;

import com.iecube.community.model.exportProgress.dto.PstReportDTO;
import com.iecube.community.model.project.entity.Project;

import java.util.List;

public interface ExportChildService {

    void processReportExportTask(String progressId, Project project, List<PstReportDTO> pstReportDTOList, Integer currentUser);

    void processGradeExportTask(String progressId, Project project, List<PstReportDTO> pstReportDTOList, Integer currentUser);
}
