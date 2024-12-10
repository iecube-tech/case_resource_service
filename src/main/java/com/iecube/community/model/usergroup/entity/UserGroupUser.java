package com.iecube.community.model.usergroup.entity;

import lombok.Data;

@Data
public class UserGroupUser {
    private Integer id;
    private Integer groupId;
    private Integer teacherId;
}
