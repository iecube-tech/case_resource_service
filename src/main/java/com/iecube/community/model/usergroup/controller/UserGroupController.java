package com.iecube.community.model.usergroup.controller;

import com.iecube.community.basecontroller.usergroup.UserGroupBaseController;
import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.usergroup.entity.UserGroup;
import com.iecube.community.model.usergroup.service.UserGroupService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/group")
public class UserGroupController extends UserGroupBaseController {

    @Autowired
    private UserGroupService userGroupService;

    @PostMapping("/add_staff_group")
    public JsonResult<Void> addStaffGroup(UserGroup userGroup, HttpSession session){
        Integer modifiedUserId = getUserIdFromSession(session);
        userGroupService.addStaffGroup(userGroup, modifiedUserId);

        return new JsonResult<>(OK);
    }

    @PostMapping("/add_customer_group")
    public JsonResult<Void> addCustomerGroup(UserGroup userGroup, HttpSession session){
        Integer modifiedUserId = getUserIdFromSession(session);
        userGroupService.addCustomerGroup(userGroup, modifiedUserId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/delete")
    public JsonResult<Void> deleteUserGroup(Integer id, HttpSession session){
        Integer modifiedUserId = getUserIdFromSession(session);
        userGroupService.deleteGroup(id, modifiedUserId);

        return new JsonResult<>(OK);
    }

    @PostMapping("/update")
    public JsonResult<Void> updateUserGroup(UserGroup userGroup, HttpSession session){
        Integer modifiedUserId = getUserIdFromSession(session);
        userGroupService.updateGroup(userGroup, modifiedUserId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/groups")
    public JsonResult<List> findByCreator(HttpSession session){
        List<UserGroup> userGroups = userGroupService.findByCreator(getUserIdFromSession(session));
        return new JsonResult<List>(OK, userGroups);
    }

    @GetMapping("/users")
    public JsonResult<List> findUsersByGroup(Integer userGroupId){
        List<User> users = userGroupService.findUsersByGroup(userGroupId);
        return new JsonResult<List>(OK, users);
    }
}
