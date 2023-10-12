package com.iecube.community.model.classification.mapper;

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
public class ClassificationMapperTests {
    @Autowired
    private ClassificationMapper classificationMapper;

    @Test
    public void insert(){
        Classification classification = new Classification();
        classification.setName("基础电路");
        classification.setParentId(1);
        classification.setProductionGroup(12);
        classification.setCreator(5);
        classification.setCreateTime(new Date());
        classification.setLastModifiedUser(5);
        classification.setLastModifiedTime(new Date());
        System.out.println(classificationMapper.insert(classification));
    }
    @Test
    public void update(){
        Classification classification = new Classification();
        classification.setId(3);
        classification.setName("基础电路");
        classification.setParentId(1);
        classification.setProductionGroup(12);
        classification.setClientele(11);
        classification.setLastModifiedUser(1);
        classification.setLastModifiedTime(new Date());
        System.out.println(classificationMapper.update(classification));
    }
    @Test
    public void delete(){
        System.out.println(classificationMapper.delete(3));
    }
    @Test
    public void findById(){
        System.out.println(classificationMapper.findById(2));
    }
    @Test
    public void findByParentId(){
        System.out.println(classificationMapper.findByParentId(1));
    }
    @Test
    public void findNameWithParenId(){
        System.out.println(classificationMapper.findNameWithParenId(1,"三电"));
    }

}
