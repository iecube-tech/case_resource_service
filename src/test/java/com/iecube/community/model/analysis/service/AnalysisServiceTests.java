package com.iecube.community.model.analysis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void t(){
        List<Integer> originalList = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            originalList.add(i);
        }

        List<Integer> result = new ArrayList<>(10);

        // 初始化结果列表，将每个范围内的数量置为0
        for (int i = 0; i < 10; i++) {
            result.add(0);
        }

        // 遍历原始列表，统计数量
        for (Integer num : originalList) {
            if (num >= 0 && num <= 100) {
                int index = (num == 100) ? 9 : num / 10;; // 确定在哪个范围内
                result.set(index, result.get(index) + 1); // 更新对应范围的数量
            }
        }

        System.out.println(result);
    }

    @Test
    public void ScoreDistributionHistogramOfCase(){
        System.out.println(analysisService.ScoreDistributionHistogramOfCase(2));
    }

    @Test
    public void ScoreDistributionHistogramOfCaseEveryTask(){
        System.out.println(analysisService.ScoreDistributionHistogramOfCaseEveryTask(2));
    }

    @Test
    public void tagCounterOfCase(){
        System.out.println(analysisService.tagCounterOfCase(2));
    }

    @Test
    public void getProjectAnalysis(){
        System.out.println(analysisService.getProjectAnalysis(12));
    }
}
