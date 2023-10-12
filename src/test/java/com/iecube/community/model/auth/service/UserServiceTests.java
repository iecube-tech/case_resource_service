package com.iecube.community.model.auth.service;

import com.iecube.community.model.auth.entity.User;
import com.iecube.community.baseservice.ex.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {

    @Autowired
    private IUserService userService;

    @Test
    public void reg(){
        try {
            User user = new User();
            user.setUsername("管理员");
            user.setPhoneNum("00000000000");
            user.setEmail("admin@iecube.com.cn");
            user.setPassword("111111");
            userService.reg(user);
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
        User user = userService.login("13759969121", "1111111");
        System.out.println(user);
    }

    @Test
    public void changePassword(){
        userService.changePassword(5, "11111111", "12345678");
    }

    @Test
    public void changPhoneNum(){
        userService.changePhoneNum(4, "13777777776", "000000");
    }

    @Test
    public void changeEmail(){
        userService.changeEmail(4, "test01Emupdate@postman.com", "000");
    }

    @Test
    public void changeUserGroup(){
        userService.changeUserGroup(4, 1, 1);
    }

    @Test
    public void changeOrganization(){
        userService.changeOrganization(4, 2, 1);
    }

    @Test
    public void changeProductDirection(){
        userService.changeProductDirection(4, 2,1);
    }

    @Test
    public void deleteUser(){
        userService.deleteUser(3,1);
    }

}
