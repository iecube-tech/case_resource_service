package com.iecube.community.model.task.entity;

import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.tag.entity.Tag;
import lombok.Data;

import java.util.List;


/**
 * 用于教师修改PST评价
 */
@Data
public class ProjectStudentTaskQo {
    Integer PSTId;
    Integer taskNum;
    String taskName;
    Integer taskGrade;
    List<Tag> taskTags;
    String taskEvaluate;
    String taskImprovement;
    String taskContent;
    Integer taskStatus;
    List<PSTResourceVo> resources;
    Integer taskResubmit;
}
