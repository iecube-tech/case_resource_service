package com.iecube.community.model.tag.vo;

import lombok.Data;

@Data
public class TeacherProjectTagVo {
    Integer id;
    Integer teacherId;
    Integer projectId;
    Integer taskNum;
    String name;
    String suggestion;
    String taskName;
}
