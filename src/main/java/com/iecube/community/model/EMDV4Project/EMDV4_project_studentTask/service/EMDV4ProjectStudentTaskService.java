package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;

import java.util.List;

public interface EMDV4ProjectStudentTaskService {
    /**
     *
     * @param projectTaskList projectTaskList
     * @param labProcList labProcList
     * @param projectStudentList projectStudentList
     * @return projectTaskList中每个task对应的tag数量
     */
    List<Integer> createProjectStudentTask(List<EMDV4ProjectTask> projectTaskList, List<BookLabCatalog> labProcList,  List<EMDV4ProjectStudent> projectStudentList);

    EMDV4ProjectStudentTask getByProjectTaskAndProjectStudent(Long projectTaskId, Long projectStudentId);

//    List<EMDV4ProjectStudentTask>
}
