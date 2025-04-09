package com.iecube.community.model.task_student_group.service;

import com.iecube.community.model.task_student_group.entity.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class TaskStudentGroupServiceTests {
    @Autowired
    private TaskStudentGroupService taskStudentGroupService;

    @Test
    public void addGroupTest(){
        Group group = new Group();
        group.setCreator(6);
        group.setTaskId(65);
        group.setName("分组1");
        group.setLimitNum(5);
        group.setLastModifiedUser(6);
        group.setCreateTime(new Date());
        group.setLastModifiedTime(new Date());
        taskStudentGroupService.addGroup(group);
    }

    @Test
    public void getGroupVoByProjectStudent(){
        System.out.println(taskStudentGroupService.getGroupVoByTaskStudent(65, 4));
    }
}
