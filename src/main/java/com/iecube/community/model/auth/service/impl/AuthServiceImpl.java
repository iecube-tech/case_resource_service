package com.iecube.community.model.auth.service.impl;

import com.iecube.community.model.auth.service.AuthService;
import com.iecube.community.model.usergroup.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserGroupService userGroupService;

    @Override
    public List<String> userAuthList(Integer teacherId) {
        // todo 添加到redis
        return userGroupService.teacherAuth(teacherId);
    }

    @Override
    public Boolean havaAuth(Integer teacherId, String authName) {
        List<String> teacherAuthList = userGroupService.teacherAuth(teacherId);
        if(teacherAuthList == null || teacherAuthList.isEmpty()){
            return false;
        }
        return teacherAuthList.contains(authName);
    }
}
