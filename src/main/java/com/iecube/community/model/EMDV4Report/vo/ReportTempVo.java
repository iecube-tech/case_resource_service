package com.iecube.community.model.EMDV4Report.vo;

import com.iecube.community.model.EMDV4Report.entity.ReportTemp;
import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import lombok.Data;

import java.util.List;

@Data
public class ReportTempVo {
    private ReportTemp reportTemp;
    private List<ReportTempChapter> reportTempChapterList;
}
