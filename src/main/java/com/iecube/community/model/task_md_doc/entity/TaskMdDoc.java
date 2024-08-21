package com.iecube.community.model.task_md_doc.entity;

import lombok.Data;

@Data
public class TaskMdDoc {
    Integer id;
    Integer mdDocId; // 对应markdown的chapterId
    Integer taskTemplateId;
    Integer taskId;
}
