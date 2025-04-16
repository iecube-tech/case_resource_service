package com.iecube.community.model.task.entity;

import com.iecube.community.model.tag.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class StudentTaskVo {
    Integer PSTId;
    Integer studentId;
    Integer taskId;
    Integer taskNum;
    String taskName;
    Double taskGrade;
    Integer taskStatus;
    List<Tag> tags;
}
