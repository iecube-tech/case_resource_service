package com.iecube.community.model.auth.mapper;

import com.iecube.community.model.auth.entity.User;
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
public class UserMapperTests {
    @Autowired
    private UserMapper userMapper;
    /**
     * 单元测试方法可以独立运行。 不用启动整个项目，提升代码测试效率
     *单元测试的特点
     * 1.必须被test注解修饰
     * 2.返回值类型必须是void
     * 3.方法的参数列表不能指定任何类型
     * 4.方法的访问修饰符必须是public
     */
    @Test
    public void insert(){
        User user = new User();
        user.setUsername("admin");
        user.setPhoneNum("18792505903");
        user.setEmail("xiaolong.zhang@iecube.com.cn");
        user.setPassword("111111");
        user.setSalt("111111");
        user.setGender(1);
        user.setCreator(0);
        user.setCreateTime(new Date());
        user.setLastModifiedUser(0);
        user.setLastModifiedTime(new Date());
        user.setTypeId(0);
        user.setUserGroup(0);
        user.setOrganizationId(0);
        user.setVipTypeId(0);
        user.setProductDirectionId(1);
        user.setIsDelete(0);

        Integer rows =  userMapper.insert(user);
        System.out.println(rows);
    }

    @Test
    public void findByPhoneNum(){
        User user = userMapper.findByPhoneNum("18792505903");
        System.out.println(user);
    }

    @Test
    public void findByEmail(){
        User user = userMapper.findByEmail("test02@postman.com");
        System.out.println(user);
    }

    @Test
    public void updatePasswordByUserId(){
        userMapper.updatePasswordByUserId(4, "12345678", 4, new Date());
    }

    @Test
    public void findByUserId(){
        System.out.println(userMapper.findByUserId(4));
    }

    @Test
    public void updatePhoneNum(){
        userMapper.updatePhoneNum(4, "13777777778", 4, new Date());
    }

    @Test
    public void updateEmail(){
        userMapper.updateEmail(4, "test01update@postman.com", 4, new Date());
    }

    @Test
    public void updateUserGroup(){
        userMapper.updateUserGroup(4, 1,4, new Date());
    }

    @Test
    public void updateOrganization(){
        userMapper.updateOrganization(4, 1,4, new Date());
    }

    @Test
    public void updateProductDirectionId(){
        userMapper.updateProductDirectionId(4, 1, 4, new Date());
    }

    @Test
    public void deleteUser(){
        userMapper.deleteUser(2, 1, new Date());
    }

    @Test
    public void findByGroup(){
        System.out.println(userMapper.findByGroup(2));
    }

    @Test
    public void findTypeIdById(){
        System.out.println(userMapper.findTypeIdById(5));
    }
}
