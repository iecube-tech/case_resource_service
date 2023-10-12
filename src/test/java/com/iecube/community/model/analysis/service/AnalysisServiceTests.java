package com.iecube.community.model.analysis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AnalysisServiceTests {

    @Autowired
    private AnalysisService analysisService;

    @Test
    public void getCurrentProjectData(){
        System.out.println(analysisService.getCurrentProjectData(12));
    }

    @Test
    public void getCaseHistoryData(){
        System.out.println(analysisService.getCaseHistoryData(12));
    }

    @Test
    public void currentProjectAverageGrade(){
        System.out.println(analysisService.currentProjectAverageGrade(12));
    }
}
