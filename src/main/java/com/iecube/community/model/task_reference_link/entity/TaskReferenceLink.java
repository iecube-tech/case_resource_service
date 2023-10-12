package com.iecube.community.model.task_reference_link.entity;

import lombok.Data;

/**
 * 任务 关联 参考链接
 */
@Data
public class TaskReferenceLink {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer referenceLinkId;
}
