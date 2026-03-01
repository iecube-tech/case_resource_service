package com.iecube.community.model.EMDV4Report.service;

import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import com.iecube.community.model.EMDV4Report.qo.ReportTempQo;
import com.iecube.community.model.EMDV4Report.vo.ReportTempVo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface EMDV4ReportService {

    List<ReportTempChapter> parseChapterFromExcel(Integer projectId, String fileName);

    ReportTempVo updateReportTemp(ReportTempQo reportTempQo, Integer creator);
}
