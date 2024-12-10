package com.iecube.community.model.teacher.service;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.teacher.dto.LoginDto;
import com.iecube.community.model.teacher.entity.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TeacherServiceTests {
    @Autowired
    private TeacherService teacherService;
    @Test
    public void reg(){
        try {
            Teacher user = new Teacher();
            user.setUsername("荀子");
            user.setEmail("xunzi@iecube.com.cn");
            user.setPassword("111111");
            user.setCollageId(1);
            teacherService.insert(user);
            System.out.println("ok");
        } catch (ServiceException e) {
            // 获取异常的具体描述信息
            System.out.println(e.getMessage());
            // 获取异常类对象 再获取类的名称
            System.out.println(e.getClass().getSimpleName());
        }
    }

    @Test
    public void login(){
        LoginDto teacher = teacherService.login("kongzi@iecube.com.cn", "111111");
        System.out.println(teacher);
    }

}
