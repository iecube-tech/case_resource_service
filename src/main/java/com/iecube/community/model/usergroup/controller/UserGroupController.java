package com.iecube.community.model.usergroup.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.usergroup.entity.UserGroup;
import com.iecube.community.model.usergroup.service.UserGroupService;
import com.iecube.community.model.usergroup.vo.UserGroupVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/group")
public class UserGroupController extends BaseController {

    @Autowired
    private UserGroupService userGroupService;

    // 查询用户组列表
    @GetMapping("/get")
    public JsonResult<List<UserGroup>> getUserGroups() {
        return new JsonResult<>(OK, userGroupService.getUserGroups());
    }

    // 查询用户组信息
    @GetMapping("/{id}")
    public JsonResult<UserGroupVo> getUserGroupVo(@PathVariable Integer id) {
        return new JsonResult<>(OK, userGroupService.getUserGroupVoById(id));
    }
    // 新增用户组
    @PostMapping("/add")
    public JsonResult<List<UserGroup>> addUserGroup(@RequestBody UserGroup userGroup) {
        return new JsonResult<>(OK, userGroupService.addUserGroup(userGroup));
    }

    @PostMapping("/update")
    public JsonResult<List<UserGroup>> updateUserGroup(@RequestBody UserGroup userGroup) {
        return new JsonResult<>(OK, userGroupService.updateUserGroup(userGroup));
    }

    @DeleteMapping("/del")
    public JsonResult<List<UserGroup>> deleteUserGroup(Integer id) {
        return new JsonResult<>(OK, userGroupService.deleteUserGroup(id));
    }


    //用户组添加用户
    @PostMapping("/{groupId}/add/teacher")
    public JsonResult<UserGroupVo> addUserToUserGroup(@PathVariable Integer groupId,
                                                      @RequestBody List<Integer> teacherIdList) {
        return new JsonResult<>(OK, userGroupService.addUserToUserGroup(groupId, teacherIdList));
    }

    //用户组删除用户
    @DeleteMapping("/{groupId}/remove/teacher/{teacherId}")
    public JsonResult<UserGroupVo> removeUserFromGroup(@PathVariable Integer groupId,
                                                       @PathVariable Integer teacherId) {
        return new JsonResult<>(OK, userGroupService.removeUserFromGroup(groupId, teacherId));
    }

    //用户组添加权限
    @PostMapping("/{groupId}/add/auth")
    public JsonResult<UserGroupVo> addAuthToGroup(@PathVariable Integer groupId,
                                                  @RequestBody List<Integer> AuthorityIds){
        return new JsonResult<>(OK, userGroupService.addAuthToGroup(groupId, AuthorityIds));
    }


    //用户组删除权限
    @DeleteMapping("/{groupId}/remove/auth/{authId}")
    public JsonResult<UserGroupVo> removeAuthFromGroup(@PathVariable Integer groupId,
                                                       @PathVariable Integer authId){
        return new JsonResult<>(OK, userGroupService.removeAuthFromGroup(groupId, authId));
    }



}
