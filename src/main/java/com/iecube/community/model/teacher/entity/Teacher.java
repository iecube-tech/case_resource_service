package com.iecube.community.model.teacher.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Teacher extends BaseEntity {
    Integer id;
    String username;
    String email;
    String password;
    String salt;
    Integer collageId;
    Integer isDelete;
}
