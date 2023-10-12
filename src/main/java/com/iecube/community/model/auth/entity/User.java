package com.iecube.community.model.auth.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

/**用户实体类**/
@Data
public class User extends BaseEntity {
    private Integer id;
    private String username;
    private String phoneNum;
    private String email;
    private String password;
    private Integer typeId;
    private String salt;
    private Integer gender;
    private Integer userGroup;
    private Integer isDelete;
    private Integer organizationId;
    private Integer vipTypeId;
    private Integer productDirectionId;

}
