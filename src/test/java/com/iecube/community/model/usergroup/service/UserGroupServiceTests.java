package com.iecube.community.model.usergroup.service;


import com.iecube.community.model.usergroup.entity.UserGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserGroupServiceTests {

    @Autowired
    private UserGroupService userGroupService;

    @Test
    public void addStaffGroup(){
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName("group04");
        userGroup.setGroupDirection(1);
        userGroup.setGroupClassification(1);
        userGroup.setGroupAuthority(1);
        userGroupService.addStaffGroup(userGroup, 1);
    }

    @Test
    public void addCustomerGroup(){
        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName("group04");
        userGroup.setGroupDirection(1);
        userGroup.setGroupClassification(1);
        userGroup.setGroupAuthority(1);
        userGroupService.addCustomerGroup(userGroup, 1);
    }

    @Test
    public void deleteGroup(){
        userGroupService.deleteGroup(2,1);
    }

    @Test
    public void findByCreator(){
        System.out.println(userGroupService.findByCreator(1));
    }

    @Test
    public void findUsersByGroup(){
        System.out.println(userGroupService.findUsersByGroup(2));
    }
}
