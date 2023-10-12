package com.iecube.community.model.usergroup.service.impl;

import com.iecube.community.model.auth.entity.User;
import com.iecube.community.model.auth.mapper.UserMapper;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.PermissionDeniedException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.usergroup.service.ex.UserGroupNotFoundException;
import com.iecube.community.model.usergroup.entity.UserGroup;
import com.iecube.community.model.usergroup.mapper.UserGroupMapper;
import com.iecube.community.model.usergroup.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户组模块业务层实现
 */
@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addStaffGroup(UserGroup userGroup, Integer lastModifiedId) {
        userGroup.setGroupType(1);  //员工
        userGroup.setIsDelete(0);
        userGroup.setCreator(lastModifiedId);
        userGroup.setCreateTime(new Date());
        userGroup.setLastModifiedUser(lastModifiedId);
        userGroup.setLastModifiedTime(new Date());
        Integer rows =  userGroupMapper.insert(userGroup);
        if (rows != 1){
            throw new InsertException("插入数据异常");
        }

    }

    @Override
    public void addCustomerGroup(UserGroup userGroup, Integer lastModifiedId) {
        userGroup.setGroupType(2);  //客户
        userGroup.setGroupAuthority(1);
        userGroup.setIsDelete(0);
        userGroup.setCreator(lastModifiedId);
        userGroup.setCreateTime(new Date());
        userGroup.setLastModifiedUser(lastModifiedId);
        userGroup.setLastModifiedTime(new Date());
        Integer rows =  userGroupMapper.insert(userGroup);
        if (rows != 1){
            throw new InsertException("插入数据异常");
        }
    }

    /**
     * 更新用户组数据
     * @param userGroup 用户组对象
     */
    @Override
    public void updateGroup(UserGroup userGroup, Integer lastModifiedId) {
        /**判断修改用户组信息的权限，只有创建者有权限修改**/
        UserGroup oldUserGroup = userGroupMapper.findById(userGroup.getId());
        Integer userGroupCreator = oldUserGroup.getCreator();
        if (userGroupCreator != lastModifiedId){
            throw new PermissionDeniedException("无权修改");
        }
        // 判断要修改的用户组是否存在
        if (oldUserGroup.getIsDelete() == 1){
            throw new UserGroupNotFoundException("用户组未找到");
        }
        // 判断修改的用户组是员工还是客户  客户的用户组权限只能是1 强制将用户组权限置1
        if (oldUserGroup.getGroupType() == 2){
            userGroup.setGroupAuthority(1);
        }
        userGroup.setLastModifiedUser(lastModifiedId);
        userGroup.setLastModifiedTime(new Date());
        Integer rows = userGroupMapper.update(userGroup);
        if (rows != 1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void deleteGroup(Integer id, Integer lastModifiedId){
        /**判断修改用户组信息的权限，只有创建者有权限删除， 删除后用户的组也要删除**/
        // 根据id查询userGroup对象
        UserGroup userGroup = userGroupMapper.findCreatorById(id);
        //判断是否删除
        if (userGroup.getIsDelete()==1){
            throw new UserGroupNotFoundException("用户组未找到");
        }
        Integer userGroupCreator = userGroup.getCreator();
        if (userGroupCreator != lastModifiedId){
            throw new PermissionDeniedException("无权删除");
        }
        Integer rows = userGroupMapper.delete(id);
        if (rows != 1){
            throw new UpdateException("更新数据异常");
        }
        // 更改用户信息  用户组内受影响的用户的用户组信息置空
        // 查询受影响的数据量
        Integer users = userGroupMapper.countUsersByUserGroup(id);
        if (users > 0){
            Integer updateRows = userGroupMapper.userUpdateUserGroup(id, lastModifiedId, new Date());
            if (users != updateRows){
                throw new UpdateException("删除用户组后跟新用户数据异常");
            }
        }

    }

    @Override
    public List<UserGroup> findByCreator(Integer creator) {
        List<UserGroup> userGroups =  userGroupMapper.findByCreator(creator);
        return userGroups;
    }

    @Override
    public List<User> findUsersByGroup(Integer groupId) {
        List<User> users =userMapper.findByGroup(groupId);
        return users;
    }


}
