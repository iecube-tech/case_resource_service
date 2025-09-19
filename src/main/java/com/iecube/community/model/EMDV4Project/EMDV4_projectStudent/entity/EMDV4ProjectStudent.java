package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EMDV4ProjectStudent {
    private Long id;
    private Integer gradeClass;
    private Integer projectId;
    private Integer studentId;
    private Double score;
    private Integer totalNumOfLabs;
    private Integer doneNumOfLabs;
    private Integer totalNumOfTags;
    private Integer achievedNumOfTags;
    private Date startTime;
    private Integer status;
    private Date doneTime;
    private Integer creator;
    private Date createTime;
    private Integer lastModifiedUser;
    private Date lastModifiedTime;
    private List<EMDV4ProjectStudentTask> studentTaskList;
}
