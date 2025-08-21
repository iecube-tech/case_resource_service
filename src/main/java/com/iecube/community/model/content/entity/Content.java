package com.iecube.community.model.content.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.List;


@Data
public class Content extends BaseEntity {

    private Integer id;
    private String name;
    private Integer parentId; // 分类
    private String cover;  // 封面
    private String introduction;  // 简介
    private String introduce; // 项目案例介绍
    private String target; //项目终极目标
    private String guidance; // 项目案例指导
    private String third;  // 0：项目案例   1：课程资源
    private String fourthType;
    private String fourth;  // 暂空
    private String keyWord; // 暂空
    private Integer packageId;
    private Integer isDelete;
    private Integer completion; // 该案例的完善度
    private Integer deviceId;
    private Integer isPrivate;
    private Integer mdCourse; // md_course的id
    private Long emdCourse;  // emd_course
    private Integer version;
    private Long emdv4Course;
}

/**
 * ********************************
 * completion  该案例的完善度字段  说明
 * ********************************
 * 教师端需要开放新增案例而引入该字段
 * 取值：
 * 0：创建content name （parentId） third introduction introduce target 具有初始值
 * 1：上传了封面图片  cover 有值
 * 2：完成了教学知识点设计
 * 3: 完成了教学匹配关系设计
 * 4：完成了案例任务设计
 * 5：完成了案例详细的操作指导
 * 6：上传了附件资源
 * 7：暂空
 * 8：暂空
 * 9：暂空
 * 10：管理员审核通过，当达到这个值后可以在所有案例中展示该案例
 */