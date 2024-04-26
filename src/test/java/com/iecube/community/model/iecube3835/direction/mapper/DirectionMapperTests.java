package com.iecube.community.model.iecube3835.direction.mapper;

import com.iecube.community.model.direction.entity.Direction;
import com.iecube.community.model.direction.mapper.DirectionMapper;
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
public class DirectionMapperTests {

    @Autowired
    private DirectionMapper directionMapper;

    @Test
    public void insert(){
        Direction direction = new Direction();
        direction.setName("能源与动力");
        direction.setPmId(5);
        direction.setProductionGroup(3);
        direction.setClientele(10);
        direction.setCreator(0);
        direction.setCreateTime(new Date());
        direction.setLastModifiedTime(new Date());
        direction.setLastModifiedUser(0);
        System.out.println(directionMapper.insert(direction));
    }

    @Test
    public void update(){
        Direction direction = new Direction();
        direction.setId(3);
        direction.setName("信息与电子");
        direction.setLastModifiedTime(new Date());
        direction.setLastModifiedUser(0);
        System.out.println(directionMapper.update(direction));
    }

    @Test
    public void delete(){
        System.out.println(directionMapper.delete(2));
    }

    @Test
    public void findById(){
        System.out.println(directionMapper.findById(1));
    }

    @Test
    public void findByName(){
        System.out.println(directionMapper.findByName("工科"));
    }

    @Test
    public void findAll(){
        System.out.println(directionMapper.findAll());
    }

}
