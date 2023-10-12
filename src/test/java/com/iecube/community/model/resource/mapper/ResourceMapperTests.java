package com.iecube.community.model.resource.mapper;

import com.iecube.community.model.resource.entity.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class ResourceMapperTests {

    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void insert(){
        Resource resource = new Resource();
        resource.setName("12023_05_26_16_41_53_QCB.png");
        resource.setFilename("12023_05_26_16_41_53_QCB.png");
        resource.setOriginFilename("12023_05_26_16_41_53_QCB.png");
        resource.setCreator(5);
        resource.setCreateTime(new Date());
        resource.setLastModifiedUser(5);
        resource.setLastModifiedTime(new Date());
        resource.setType("image");
        System.out.println(resourceMapper.insert(resource));
    }

    @Test
    public void get(){
        System.out.println(resourceMapper.getByName("ww"));
    }

}
