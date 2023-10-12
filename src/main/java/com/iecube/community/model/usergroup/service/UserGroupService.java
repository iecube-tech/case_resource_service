package com.iecube.community.model.usergroup.service;

import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.usergroup.entity.UserGroup;

import java.util.List;

public interface UserGroupService {
    void addStaffGroup(UserGroup userGroup, Integer lastModifiedId);

    void addCustomerGroup(UserGroup userGroup, Integer lastModifiedId);

    void updateGroup(UserGroup userGroup, Integer lastModifiedId);

    void deleteGroup(Integer id, Integer lastModifiedId);

    List<UserGroup> findByCreator(Integer creator);

    List<User> findUsersByGroup(Integer groupId);

}
