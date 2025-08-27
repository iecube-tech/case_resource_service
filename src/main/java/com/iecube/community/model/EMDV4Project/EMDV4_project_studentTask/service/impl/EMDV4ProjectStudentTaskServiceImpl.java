package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.impl;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper.EMDV4ProjectStudentTaskMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.model.auth.service.ex.InsertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EMDV4ProjectStudentTaskServiceImpl implements EMDV4ProjectStudentTaskService {

    @Autowired
    private EMDV4ProjectStudentTaskMapper emdV4ProjectStudentTaskMapper;

    @Autowired
    private EMDV4StudentTaskBookService emdV4StudentTaskBookService;

    @Override
    public List<Integer> createProjectStudentTask(List<EMDV4ProjectTask> projectTaskList, List<BookLabCatalog> labProcList, List<EMDV4ProjectStudent> projectStudentList) {
        // 5. 根据学生人数 创建 EMDV4_student_task_book(实验指导书)和EMDV4_component(组件) // 分发实验指导书 一个学生一个学生的穿件 循环学生人数次
        // projectStudent+projectStudentTask->taskBook
        // List<EMDV4ProjectStudent>元素 和 List<EMDV4ProjectTask> 元素两两组合
        List<EMDV4ProjectStudentTask> studentTaskList = new ArrayList<>();
        List<Integer> taskTagNumList = new ArrayList<>(); // 第几个task的tag数量
        for(int i = 0; i < projectTaskList.size(); i++){
            taskTagNumList.add(0);
        }
        for(EMDV4ProjectStudent projectStudent : projectStudentList){
            for(int i=0; i<projectTaskList.size(); i++){
                EMDV4ProjectTask projectTask = projectTaskList.get(i);
                BookLabCatalog labCatalog = labProcList.get(i);
                EMDV4StudentTaskBook studentTaskBook = emdV4StudentTaskBookService.createStudentTaskBook(labCatalog);
                EMDV4ProjectStudentTask studentTask = this.newEMDV4ProjectStudentTask(projectStudent,projectTask,studentTaskBook);
                studentTask.setTotalNumOfTags(studentTaskBook.getTagList().size());  // 更新实验下的总tag数量
                taskTagNumList.set(i, studentTaskBook.getTagList().size());
                studentTaskList.add(studentTask);
            }
        }
        int res = emdV4ProjectStudentTaskMapper.batchInsert(studentTaskList);
        if(res!= studentTaskList.size()){
            throw new InsertException("分配学生实验异常");
        }
        return taskTagNumList;
    }

    private EMDV4ProjectStudentTask newEMDV4ProjectStudentTask(EMDV4ProjectStudent projectStudent, EMDV4ProjectTask projectTask, EMDV4StudentTaskBook studentTaskBook){
        EMDV4ProjectStudentTask projectStudentTask = new EMDV4ProjectStudentTask();
        projectStudentTask.setProjectStudent(projectStudent.getId());
        projectStudentTask.setProjectTask(projectTask.getId());
        projectStudentTask.setTaskBookId(studentTaskBook.getId());
        projectStudentTask.setScore(0.0);
        projectStudentTask.setStatus(0);
        projectStudentTask.setStartTime(null);
        projectStudentTask.setDoneTime(null);
        projectStudentTask.setUseTime(null);
        projectStudentTask.setTotalNumOfTags(0);
        projectStudentTask.setAchievedNumOfTags(0);
        projectStudentTask.setAverageErrorRate(0.0);
        return projectStudentTask;
    }

}
