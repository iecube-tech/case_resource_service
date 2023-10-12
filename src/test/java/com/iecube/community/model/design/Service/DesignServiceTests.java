package com.iecube.community.model.design.Service;


import com.iecube.community.model.design.entity.KnowledgePoint;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.Design;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class DesignServiceTests {
    @Autowired
    private DesignService designService;

    @Test
    public void getCaseDesign(){
        System.out.println(designService.getCaseDesign(2));
    }

    @Test
    public void addCaseDesign(){
        Design design = new Design();
        design.setTargetName("能够正确采集、处理实验数据，对实验结果进行分析和解释，并通过信息综合得到合理有效的结论。");
        List<KnowledgePoint> knowledgePointList = new ArrayList<>();
        KnowledgePoint knowledgePoint1=new KnowledgePoint();
        knowledgePoint1.setPoint("功放电路测试结果分析，对比，得出电路设计的合理性");
        knowledgePointList.add(knowledgePoint1);
        design.setKnowledgePoints(knowledgePointList);
        designService.addCaseDesign(2,design);
    }
}
