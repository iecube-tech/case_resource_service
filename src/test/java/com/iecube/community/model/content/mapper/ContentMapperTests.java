package com.iecube.community.model.content.mapper;

import com.iecube.community.model.content.entity.Content;
import com.iecube.community.model.content.entity.taskTemplates;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@SpringBootTest 表示标注当前的类是一个测试类， 不会随同项目一块打包发送
@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class ContentMapperTests {

    @Autowired
    private ContentMapper contentMapper;

    @Test
    public void add(){
        Content content = new Content();
        content.setName("降噪耳机");
        content.setParentId(1);
        content.setCover("bbbbbb");
        content.setIntroduction("简介");
        content.setThird("详细的实现流程");
        content.setFourth("结构关系");
        content.setKeyWord("关键字");
        content.setPackageId(1);
        content.setIsDelete(0);
        content.setCreator(10);
        content.setCreateTime(new Date());
        content.setLastModifiedTime(new Date());
        content.setLastModifiedUser(10);
//        System.out.println(contentMapper.insert(content));
    }

    @Test
    public void update(){
        Content content = new Content();
        content.setId(1);
        content.setName("降噪耳机");
        content.setParentId(1);
        content.setCover("ccccccc");
        content.setIntroduction("简介");
        content.setIntroduce("课程设计思路");
        content.setGuidance("工程教育认证标准匹配关系");
        content.setThird("详细的实现流程");
        content.setFourth("结构关系");
        content.setKeyWord("关键字");
        content.setPackageId(1);
        content.setIsDelete(0);
        content.setCreator(10);
        content.setCreateTime(new Date());
        content.setLastModifiedTime(new Date());
        content.setLastModifiedUser(10);
        System.out.println(contentMapper.update(content));
    }
    @Test
    public void delete(){
        System.out.println(contentMapper.delete(1,1, new Date()));
    }

    @Test
    public void getTeacherCreate(){
        System.out.println(contentMapper.getTeacherCreate(6));
    }


    @Test
    public void findById(){
        System.out.println(contentMapper.findById(1));
    }

    @Test
    public void findByParentId(){
        System.out.println(contentMapper.findByParentId(1));
    }

    @Test
    public void findNoDelByParentId(){
        System.out.println(contentMapper.findNotDelByParentId(1));
    }

    @Test
    public void findAll(){
        System.out.println(contentMapper.findAll());
    }

    @Test
    public void contentAddTask(){
        taskTemplates taskTemplates = new taskTemplates();
        taskTemplates.setContentId(2);
        taskTemplates.setTaskName("任务一");
        List taskTargets = new ArrayList<>();
        taskTargets.add("111111");
        taskTargets.add("1111111");
        System.out.println(taskTargets);
        taskTemplates.setTaskTargets(taskTargets);
        List taskDeliverables = new ArrayList();
        taskDeliverables.add("111111");
        taskDeliverables.add("1111");
        taskTemplates.setTaskDeliverables(taskDeliverables);
        System.out.println(contentMapper.contentAddTaskTemplate(taskTemplates));
    }

    @Test
    public void findTaskTemplatesByContentId(){
        System.out.println(contentMapper.findTaskTemplatesByContentId(2));
    }

    @Test
    public void findResourcesById(){
        System.out.println(contentMapper.findResourcesById(2));
    }

    @Test
    public void listTest(){
        List<Integer> oldModules = new ArrayList<>();
        oldModules.add(1);
        oldModules.add(2);
        oldModules.add(3);
        oldModules.add(4);
        List<Integer> newModules = new ArrayList<>();
        newModules.add(3);
        newModules.add(4);
        newModules.add(5);
        newModules.add(6);
        oldModules.removeAll(newModules);
        System.out.println(oldModules);
        System.out.println(oldModules.get(1));
    }
}
