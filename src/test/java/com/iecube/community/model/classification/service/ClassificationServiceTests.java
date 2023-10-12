package com.iecube.community.model.classification.service;

import com.iecube.community.model.classification.entity.Classification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class ClassificationServiceTests {
    @Autowired
    private ClassificationService classificationService;

    @Test
    public void insert(){
        Classification classification = new Classification();
        classification.setName("基础电路");
        classification.setParentId(1);
        classification.setProductionGroup(12);
        classificationService.insert(classification,5);
    }
    @Test
    public void update(){
        Classification classification = new Classification();
        classification.setId(4);
        classification.setName("数字电路");
        classification.setParentId(1);
        classification.setProductionGroup(12);
        classification.setClientele(11);
        classificationService.update(classification,5);
    }
    @Test
    public void delete(){
        classificationService.delete(5,5);
    }
    @Test
    public void findById(){
        System.out.println(classificationService.findById(2));
    }
    @Test
    public void findByParentId(){
        System.out.println(classificationService.findByParentId(1));
    }

}
