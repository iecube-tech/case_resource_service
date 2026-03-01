package com.iecube.community.model.EMDV4Report.mapper;

import com.iecube.community.model.EMDV4Report.entity.ReportTemp;
import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ReportMapper {
    int insertReportTemp(ReportTemp reportTemp);

    int batchInsertReportTempChapter(List<ReportTempChapter> list);

    ReportTemp selectReportTempById(Long id);

    int updateReportTempById(ReportTemp reportTemp);

    int deleteReportTempById(Long id);

    int deleteReportTempChapterById(Long id);

    int deleteReportTempChapterByTemp(Long reportTempId);
}
