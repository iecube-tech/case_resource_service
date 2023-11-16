package com.iecube.community.model.taskTemplate.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class TaskTemplate extends BaseEntity {
    Integer id;
    Integer contentId;
    Integer num; //用于一个项目下的任务排序
    Double weighting;
    String taskName;
    String taskCover;
}
