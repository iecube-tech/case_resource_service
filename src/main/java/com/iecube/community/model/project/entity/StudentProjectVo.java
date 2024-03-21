package com.iecube.community.model.project.entity;

import com.iecube.community.model.task.entity.Task;
import com.iecube.community.model.task.entity.TaskVo;
import lombok.Data;

import java.util.List;

/**
 *学生端任务详情
 */
@Data
public class StudentProjectVo {
    String projectName;
    String projectIntroduce;
    String projectIntroduction;
    String projectTarget;
    String projectCover;
    Integer projectDeviceId;
    List<TaskVo> projectTaskList;
}
