package com.iecube.community.model.usergroup.mapper;

import com.iecube.community.model.usergroup.entity.UserGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserGroupMapperTests {
    @Autowired
    private UserGroupMapper userGroupMapper;

    @Test
    public void insert(){
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName("group3");
        userGroup.setGroupType(1);
        userGroup.setGroupDirection(1);
        userGroup.setGroupAuthority(1);
        userGroup.setLastModifiedUser(1);
        userGroup.setLastModifiedTime(new Date());
        userGroup.setCreator(1);
        userGroup.setCreateTime(new Date());
        userGroup.setIsDelete(0);
        userGroupMapper.insert(userGroup);
    }

    @Test
    public void update(){
        UserGroup userGroup = new UserGroup();
        userGroup.setId(3);
        userGroup.setGroupName("group33");
        userGroup.setGroupDirection(1);
        userGroup.setGroupClassification(2);
        userGroup.setGroupAuthority(1);
        userGroup.setLastModifiedUser(1);
        userGroup.setLastModifiedTime(new Date());
        userGroupMapper.update(userGroup);
    }

    @Test
    public void delete(){
        userGroupMapper.delete(3);
    }

    @Test
    public void findCreatorById(){
        UserGroup userGroup = userGroupMapper.findCreatorById(3);
        System.out.println(userGroup);
        System.out.println(userGroup.getCreator());
    }

    @Test
    public void findById(){
        UserGroup userGroup = userGroupMapper.findById(3);
        System.out.println(userGroup);
    }

    @Test
    public void userUpdateUserGroup(){
        Integer rows = userGroupMapper.userUpdateUserGroup(0, 1, new Date());
        System.out.println(rows);
    }

    @Test
    public void countUsersByUserGroup(){
        System.out.println(userGroupMapper.countUsersByUserGroup(2));
    }

    @Test
    public void findByCreator(){
        System.out.println(userGroupMapper.findByCreator(1));
    }
}
