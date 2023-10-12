package com.iecube.community.model.task_requirement.entity;

import lombok.Data;

/**
 * 任务 关联 任务要求
 */
@Data
public class TaskRequirement {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer requirementId;
}
