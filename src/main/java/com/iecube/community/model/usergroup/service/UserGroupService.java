package com.iecube.community.model.usergroup.service;

import com.iecube.community.model.usergroup.entity.UserGroup;
import com.iecube.community.model.usergroup.vo.UserGroupVo;

import java.util.List;

public interface UserGroupService {

    List<UserGroup> getUserGroups();

    List<UserGroup> addUserGroup(UserGroup userGroup);

    List<UserGroup> updateUserGroup(UserGroup userGroup);

    List<UserGroup> deleteUserGroup(Integer groupId);

    UserGroupVo getUserGroupVoById(Integer id);

    UserGroupVo addUserToUserGroup(Integer groupId, List<Integer> teacherIdList);

    UserGroupVo removeUserFromGroup(Integer groupId, Integer teacherId);

    UserGroupVo addAuthToGroup(Integer groupId, List<Integer> authorityIds);

    UserGroupVo removeAuthFromGroup(Integer groupId, Integer authorityId);

    List<String> teacherAuth(Integer teacherId);

}
