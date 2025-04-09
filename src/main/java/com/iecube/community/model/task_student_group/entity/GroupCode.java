package com.iecube.community.model.task_student_group.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupCode {
    Integer id;
    Integer groupId;
    String code;
    Date createTime;
    Date unableTime;
}
