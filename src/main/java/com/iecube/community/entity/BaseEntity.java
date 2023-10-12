package com.iecube.community.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**作为实体类的基类**/
@Data
public class BaseEntity implements Serializable {
    private String creatorType;
    private Integer creator;
    private Date createTime;
    private Integer lastModifiedUser;
    private Date lastModifiedTime;
}
