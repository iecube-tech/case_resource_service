package com.iecube.community.model.design.mapper;

import com.iecube.community.model.design.entity.CaseTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class DesignMapperTests {

    @Autowired
    private DesignMapper designMapper;

    @Test
    public void addCaseTargetTest(){
        CaseTarget caseTarget = new CaseTarget();
        caseTarget.setCaseId(2);
        caseTarget.setTargetName("能够用方框图、电路原理图、程序流程图或设计报告等形式正确表达一个工程问题的解决方案，并使用仪器或工具软件对电路进行分析、仿真或辅助设计。");
        designMapper.addCaseTarget(caseTarget);
    }

    @Test
    public void getCaseTargetsByCase(){
        System.out.println(designMapper.getCaseTargetsByCase(2));
    }

    @Test
    public void getKnowledgePointsByTargetId(){
        System.out.println(designMapper.getKnowledgePointsByTargetId(2));
    }

}
