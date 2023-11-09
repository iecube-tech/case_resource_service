package com.iecube.community.model.task_back_drop.entity;

import lombok.Data;

/**
 * task / taskTemplate 关联 任务背景 实体类
 */
@Data
public class TaskBackDrop {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer backDropId;
}
