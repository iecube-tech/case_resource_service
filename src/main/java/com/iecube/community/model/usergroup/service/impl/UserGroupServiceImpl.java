package com.iecube.community.model.usergroup.service.impl;

import com.iecube.community.model.auth.entity.Authority;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.teacher.vo.TeacherVo;
import com.iecube.community.model.usergroup.entity.UserGroup;
import com.iecube.community.model.usergroup.mapper.UserGroupMapper;
import com.iecube.community.model.usergroup.service.UserGroupService;
import com.iecube.community.model.usergroup.vo.UserGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Override
    public List<UserGroup> getUserGroups() {
        return userGroupMapper.getUserGroups();
    }

    @Override
    public UserGroupVo getUserGroupVoById(Integer id) {
        UserGroup group = userGroupMapper.getUserGroupById(id);
        List<TeacherVo> teacherVoList = userGroupMapper.getTeachersByGroupId(id);
        List<Authority> authorityList = userGroupMapper.getAuthoritiesByGroupId(id);
        UserGroupVo vo = new UserGroupVo();
        vo.setId(group.getId());
        vo.setName(group.getName());
        vo.setAuthorities(authorityList);
        vo.setTeacherList(teacherVoList);
        return vo;
    }

    @Override
    public List<UserGroup> addUserGroup(UserGroup userGroup) {
        int res = userGroupMapper.addUserGroup(userGroup);
        if(res!=1){
            throw new InsertException("添加数据异常");
        }
        return userGroupMapper.getUserGroups();
    }

    @Override
    public List<UserGroup> updateUserGroup(UserGroup userGroup) {
        int res = userGroupMapper.updateUserGroup(userGroup);
        if(res!=1){
            throw new InsertException("更新数据异常");
        }
        return userGroupMapper.getUserGroups();
    }

    @Override
    public List<UserGroup> deleteUserGroup(Integer groupId) {
        int res = userGroupMapper.deleteUserGroup(groupId);
        if(res!=1){
            throw new InsertException("删除数据异常");
        }
        return userGroupMapper.getUserGroups();
    }

    @Override
    public UserGroupVo addUserToUserGroup(Integer groupId, List<Integer> teacherIdList) {
        teacherIdList.forEach(teacherId -> {
            int res = userGroupMapper.addTeacherToGroup(groupId, teacherId);
            if(res!=1){
                throw new InsertException("添加数据异常");
            }
        });
        return getUserGroupVoById(groupId);
    }

    @Override
    public UserGroupVo removeUserFromGroup(Integer groupId, Integer teacherId) {
        int res = userGroupMapper.deleteTeacherFromGroup(groupId, teacherId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return getUserGroupVoById(groupId);
    }

    @Override
    public UserGroupVo addAuthToGroup(Integer groupId, List<Integer> authorityIds) {
        authorityIds.forEach(authorityId -> {
            int res = userGroupMapper.addAuthorityToGroup(groupId, authorityId);
            if(res!=1){
                throw new InsertException("添加数据异常");
            }
        });
        return getUserGroupVoById(groupId);
    }

    @Override
    public UserGroupVo removeAuthFromGroup(Integer groupId, Integer authorityId) {
        int res = userGroupMapper.deleteAuthorityFromGroup(groupId, authorityId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
        return getUserGroupVoById(groupId);
    }

    @Override
    public List<String> teacherAuth(Integer teacherId) {
        return userGroupMapper.getTeacherAuth(teacherId);
    }
}
