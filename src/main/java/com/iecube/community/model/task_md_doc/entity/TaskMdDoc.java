package com.iecube.community.model.task_md_doc.entity;

import lombok.Data;

@Data
public class TaskMdDoc {
    Integer id;
    Integer mdDocId;
    Integer taskTemplateId;
    Integer taskId;
}
