package com.iecube.community.model.task.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iecube.community.model.pst_resource.entity.PSTResourceVo;
import com.iecube.community.model.tag.entity.Tag;
import lombok.Data;

import java.util.List;


/**
 * 用于教师修改PST评价
 */
@Data
public class ProjectStudentTaskQo {
    @JsonProperty("PSTId")
    private Integer PSTId;
    private Integer taskNum;
    private String taskName;
    private Double taskGrade;
    private List<Tag> taskTags;
    private String taskEvaluate;
    private String taskImprovement;
    private String taskContent;
    private Integer taskStatus;
    private List<PSTResourceVo> resources;
    private Integer taskResubmit;
}
