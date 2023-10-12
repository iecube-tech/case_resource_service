package com.iecube.community.model.task_reference_file.entity;

import lombok.Data;

/**
 * 任务/任务模板 关联 参考文件
 */
@Data
public class TaskReferenceFile {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer referenceFileId;
}
