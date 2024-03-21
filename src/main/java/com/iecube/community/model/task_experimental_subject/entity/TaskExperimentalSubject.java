package com.iecube.community.model.task_experimental_subject.entity;

import lombok.Data;

@Data
public class TaskExperimentalSubject {
    Integer id;
    Integer taskId;
    Integer taskTemplateId;
    Integer experimentalSubjectId;
}
