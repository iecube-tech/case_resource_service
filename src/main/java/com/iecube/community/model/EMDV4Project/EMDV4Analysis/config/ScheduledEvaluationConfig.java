package com.iecube.community.model.EMDV4Project.EMDV4Analysis.config;

import com.iecube.community.model.EMDV4Project.EMDV4Analysis.service.EMDV4AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledEvaluationConfig {

    @Autowired
    private EMDV4AnalysisService emdv4AnalysisService;

    @Scheduled(cron = "${AIEvaluation.cron}")
    public void genEvaluation() {
        log.info("分析评价定时启动");
        emdv4AnalysisService.allEvaluationGen();
    }
}
