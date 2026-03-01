package com.iecube.community.model.EMDV4Report.service.impl;

import com.iecube.community.model.EMDV4Report.entity.ReportTemp;
import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import com.iecube.community.model.EMDV4Report.mapper.EMDV4ReportMapper;
import com.iecube.community.model.EMDV4Report.qo.ReportTempQo;
import com.iecube.community.model.EMDV4Report.service.EMDV4ReportService;
import com.iecube.community.model.EMDV4Report.vo.ReportTempVo;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EMDV4ReportServiceImpl implements EMDV4ReportService {

    @Autowired
    private EMDV4ReportMapper reportMapper;


    @Override
    public List<ReportTempChapter> parseChapterFromExcel(Integer projectId, String fileName) {
        return List.of();
    }

    public ReportTempVo saveReportTemp(ReportTempQo reportTempQo, Integer creator) {
        ReportTemp reportTemp = new ReportTemp();
        reportTemp.setProjectId(reportTempQo.getProjectId());
        reportTemp.setName(reportTempQo.getName());
        reportTemp.setChapterSize(reportTempQo.getChapterList().size());
        reportTemp.setStatus(ReportTemp.TempStatus.SAVED);
        reportTemp.setCreator(creator);
        reportTemp.setCreateTime(new Date());
        reportTemp.setLastModifiedTime(new Date());
        reportTemp.setLastModifiedUser(creator);

        int res = reportMapper.insertReportTemp(reportTemp);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        List<ReportTempChapter> reportTempChapterList = this.handleChapterQo(reportTempQo,reportTemp.getId());
        ReportTempVo reportTempVo = new ReportTempVo();
        reportTempVo.setReportTemp(reportTemp);
        reportTempVo.setReportTempChapterList(reportTempChapterList);
        return reportTempVo;
    }

    @Override
    public ReportTempVo updateReportTemp(ReportTempQo reportTempQo, Integer creator){
        if(reportTempQo.getId()==null){
            return this.saveReportTemp(reportTempQo, creator);
        }
        ReportTemp exist = reportMapper.selectReportTempById(reportTempQo.getId());
        exist.setName(reportTempQo.getName());
        exist.setLastModifiedTime(new Date());
        int res = reportMapper.updateReportTempById(exist);
        if (res != 1 ){
            throw new UpdateException("更新数据异常");
        }
        reportMapper.deleteReportTempChapterByTemp(reportTempQo.getId());
        List<ReportTempChapter> reportTempChapterList = this.handleChapterQo(reportTempQo, reportTempQo.getId());
        ReportTempVo reportTempVo = new ReportTempVo();
        reportTempVo.setReportTemp(exist);
        reportTempVo.setReportTempChapterList(reportTempChapterList);
        return reportTempVo;
    }

    private List<ReportTempChapter> handleChapterQo(ReportTempQo reportTempQo, Long tempId){
        List<ReportTempChapter> reportTempChapterList = new ArrayList<>();
        reportTempQo.getChapterList().forEach(chapter -> {
            ReportTempChapter reportTempChapter = new ReportTempChapter();
            reportTempChapter.setReportTempId(tempId);
            reportTempChapter.setTitle(chapter.getTitle());
            reportTempChapter.setOrder(chapter.getOrder());
            reportTempChapter.setType(chapter.getType());
            reportTempChapter.setRequired(chapter.getRequired());
        });
        int res2 = reportMapper.batchInsertReportTempChapter(reportTempChapterList);
        if(res2!=reportTempChapterList.size()){
            throw new InsertException("新增段落数据异常");
        }
        return reportTempChapterList;
    }


}
