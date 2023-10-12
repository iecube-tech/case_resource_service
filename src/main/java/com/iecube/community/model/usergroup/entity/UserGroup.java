package com.iecube.community.model.usergroup.entity;


import com.iecube.community.entity.BaseEntity;
import lombok.Data;

/**userGroup 实体类**/
@Data
public class UserGroup extends BaseEntity {
    private Integer id;
    private String groupName;
    private Integer groupType;    // 1 员工  2 客户
    private Integer groupDirection;
    private Integer groupClassification;
    private Integer groupAuthority;
    private Integer isDelete;
}
