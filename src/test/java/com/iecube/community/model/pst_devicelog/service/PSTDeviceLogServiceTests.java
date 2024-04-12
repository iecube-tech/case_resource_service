package com.iecube.community.model.pst_devicelog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class PSTDeviceLogServiceTests {
    @Autowired
    private PSTDeviceLogService pstDeviceLogService;

    @Test
    public void getProjectLogCompareTest(){
        System.out.println(pstDeviceLogService.getProjectLogCompare(64));
    }
}
