package com.iecube.community.model.npoints.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class NPointsServiceTests {

    @Autowired
    private NPointsService nPointsService;

    @Test
    public void getNodesByCaseId(){
        System.out.println(nPointsService.getNodesByCaseId(1));
    }

    @Test
    public void getNodesByModuleId(){
        System.out.println(nPointsService.getNodesByModuleId(1));
    }

    @Test
    public void getNodesByConceptId(){
        System.out.println((nPointsService.getNodesByConceptId(1)));
    }
}
