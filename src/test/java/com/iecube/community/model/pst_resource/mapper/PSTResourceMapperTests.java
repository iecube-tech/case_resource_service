package com.iecube.community.model.pst_resource.mapper;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class PSTResourceMapperTests {
    @Autowired PSTResourceMapper pstResourceMapper;

    @Test
    public void getPSTResourcesByPSTId(){
        System.out.println(pstResourceMapper.getPSTResourcesByPSTId(171));
    }

}
