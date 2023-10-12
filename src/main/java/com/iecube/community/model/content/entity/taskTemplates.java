package com.iecube.community.model.content.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class taskTemplates extends BaseEntity {
    Integer id;
    Integer contentId;
    Integer num; //用于一个项目下的任务排序
    String taskName;
    String taskCover;
    List<String> taskTargets;
    List<String> taskDeliverables;
    Date taskStartTime;
    Date taskEndTime;
}
