package com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.mapper.EMDV4ProjectTaskMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.service.EMDV4ProjectTaskService;
import com.iecube.community.model.EMDV4Project.project.qo.EMDV4ProjectQo;
import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.task.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EMDV4ProjectTaskImpl implements EMDV4ProjectTaskService {

    @Autowired
    private EMDV4ProjectTaskMapper emdV4ProjectTaskMapper;

    @Override
    public List<EMDV4ProjectTask> projectTaskListCreate(Project project, EMDV4ProjectQo emdv4ProjectQo, List<StudentDto> studentDtoList) {
        List<EMDV4ProjectTask> taskV4List = new ArrayList<>();
        List<Task> taskTempList = emdv4ProjectQo.getTask();
        taskTempList.forEach(taskTemp -> {
            EMDV4ProjectTask task = new EMDV4ProjectTask();
            task.setProjectId(project.getId());
            task.setLabId(taskTemp.getLabProcId());
            task.setNum(taskTemp.getNum());
            task.setName(taskTemp.getTaskName());
            task.setClasshour(taskTemp.getClassHour());
            task.setWeighting(taskTemp.getWeighting());
            task.setCoefficientOfDifficulty(null);
            task.setStartTime(taskTemp.getTaskStartTime());
            task.setEndTime(taskTemp.getTaskEndTime());
            task.setStatus(0);
            task.setDoneTime(null);
            task.setTotalNumOfStudent(studentDtoList.size());
            task.setDoneNumOfStudent(0);
            task.setAverageScore(0.0);
            task.setAverageUseTime(0.0);
            task.setAverageErrorRate(0.0);
            taskV4List.add(task);
        });
        int res = emdV4ProjectTaskMapper.batchInsert(taskV4List);
        if(res!= taskV4List.size()){
            throw new InsertException("课程实验发布错误");
        }
        return emdV4ProjectTaskMapper.getByProjectId(project.getId());
    }

    @Override
    public void updateProjectTaskStudentNum(Integer projectId, Integer studentNum) {
        emdV4ProjectTaskMapper.updateProjectStudentNums(projectId, studentNum);
    }
}
