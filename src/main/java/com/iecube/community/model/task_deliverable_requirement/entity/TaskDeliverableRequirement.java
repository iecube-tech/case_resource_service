package com.iecube.community.model.task_deliverable_requirement.entity;

import lombok.Data;

/**
 * 任务/任务模板 关联 任务交付物要求 实体类
 */
@Data
public class TaskDeliverableRequirement {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer deliverableRequirementId;
}
