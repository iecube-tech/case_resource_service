package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity;

import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import lombok.Data;

import java.util.Date;

@Data
public class EMDV4ProjectStudentTask {
    private Long id;
    private Long projectStudent;
    private Long projectTask;
    private String taskBookId;
    private Double score;
    private Integer status;
    private Date startTime;
    private Date doneTime;
    private Double useTime;
    private Integer totalNumOfTags;
    private Integer achievedNumOfTags;
    private Double averageErrorRate;
    private EMDV4StudentTaskBook studentTaskBook;
}
