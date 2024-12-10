package com.iecube.community.model.usergroup.mapper;

import com.iecube.community.model.auth.entity.Authority;
import com.iecube.community.model.teacher.vo.TeacherVo;
import com.iecube.community.model.usergroup.entity.UserGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserGroupMapper {

    List<UserGroup> getUserGroups();

    UserGroup getUserGroupById(Integer id);

    Integer addUserGroup(UserGroup userGroup);

    Integer updateUserGroup(UserGroup userGroup);

    Integer deleteUserGroup(Integer id);

    List<TeacherVo> getTeachersByGroupId(Integer groupId);

    Integer addTeacherToGroup(Integer groupId, Integer teacherId);

    Integer deleteTeacherFromGroup(Integer groupId, Integer teacherId);

    List<Authority> getAuthoritiesByGroupId(Integer groupId);

    Integer addAuthorityToGroup(Integer groupId, Integer authorityId);

    Integer deleteAuthorityFromGroup(Integer groupId, Integer authorityId);

    List<String> getTeacherAuth(Integer teacherId);
}
