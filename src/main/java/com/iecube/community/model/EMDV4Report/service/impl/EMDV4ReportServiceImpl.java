package com.iecube.community.model.EMDV4Report.service.impl;

import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import com.iecube.community.model.EMDV4Report.mapper.EMDV4ReportMapper;
import com.iecube.community.model.EMDV4Report.service.EMDV4ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EMDV4ReportServiceImpl implements EMDV4ReportService {

    @Autowired
    private EMDV4ReportMapper reportMapper;


    @Override
    public List<ReportTempChapter> parseChapterFromExcel(Integer projectId, String fileName) {
        return List.of();
    }
}
