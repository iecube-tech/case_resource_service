package com.iecube.community.model.analysis.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AnalysisMapperTests {
    @Autowired
    private AnalysisMapper analysisMapper;

    @Test
    public void getProjectNumByCaseId(){
        System.out.println(analysisMapper.getProjectNumByCase(2));
    }

    @Test
    public  void getProjectIdListByCaseId(){
        System.out.println(analysisMapper.getProjectIdListByCaseId(12));
    }
}
