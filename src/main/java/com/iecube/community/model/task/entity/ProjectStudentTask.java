package com.iecube.community.model.task.entity;

import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.entity.ResourceVo;
import lombok.Data;

import java.util.List;

@Data
public class ProjectStudentTask {
    Integer id;
    Integer projectId;
    Integer taskId;
    Integer studentId;
    Double grade;
    List<String> tags;
    String evaluate;
    String improvement;
    String content;
    Integer status;
    List<Resource> resources;
    Integer resubmit;
}
