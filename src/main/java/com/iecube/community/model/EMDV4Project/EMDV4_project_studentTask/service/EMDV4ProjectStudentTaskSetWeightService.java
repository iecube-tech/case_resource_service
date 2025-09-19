package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;

import java.util.List;

public interface EMDV4ProjectStudentTaskSetWeightService {

    void checkProjectTaskWeighting(List<EMDV4StudentTaskBook> targetBlockList, EMDV4ProjectStudentTask PST);
}
