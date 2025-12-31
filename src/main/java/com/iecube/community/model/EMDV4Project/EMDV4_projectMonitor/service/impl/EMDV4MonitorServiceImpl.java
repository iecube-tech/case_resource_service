package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.TaskStepDto;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.mapper.EMDV4ProjectMonitorMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.EMDV4MonitorService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class EMDV4MonitorServiceImpl implements EMDV4MonitorService {

    @Autowired
    private EMDV4ProjectMonitorMapper monitorMapper;


    @Override
    public MonitorInfoVo getMonitorInfo(Integer projectId) {
        MonitorInfoVo res = monitorMapper.getMonitorInfo(projectId);
        if(res==null){
            throw new ServiceException("未找到相关数据");
        }
        List<MonitorInfoVo.TaskInfo> taskInfoList = this.taskOverview(projectId);
        res.setTaskInfoList(taskInfoList);
        res.setLastUpdateTime(new Date());
        return res;
    }

    @Override
    public void getStuMonitorPaging(Integer page, Integer pageSize) {

    }

    private List<MonitorInfoVo.TaskInfo> taskOverview(Integer projectId) {
        List<MonitorInfoVo.TaskInfo> resList = new ArrayList<>();

        List<TaskStepDto> taskStepDtoList = monitorMapper.getAllStuTaskStep(projectId);
        if(taskStepDtoList == null || taskStepDtoList.isEmpty()) {
            throw new ServiceException("未找到相关数据");
        }

        // project 的 完成率 完成人数 进行中 未开始
        Map<Long, List<TaskStepDto>> stuMap = taskStepDtoList.stream().collect(Collectors.groupingBy(TaskStepDto::getPsId));
        AtomicInteger doneNum = new AtomicInteger();
        AtomicInteger doingNum = new AtomicInteger();
        AtomicInteger notStartedNum = new AtomicInteger();
        stuMap.forEach((psId, dtoList)->{
            int res = dtoList.stream().mapToInt(TaskStepDto::getBlockStatus).sum();
            if(res==0){
                notStartedNum.getAndIncrement();
            }else if(res>=1 && res<dtoList.size()){
                doingNum.getAndIncrement();
            }else if(res==dtoList.size()){
                doneNum.getAndIncrement();
            }
        });
        MonitorInfoVo.TaskInfo pInfo = new MonitorInfoVo.TaskInfo();
        pInfo.setTaskName("所有实验（概览）");
        pInfo.setIsProject(true);
        pInfo.setDoneNum(doneNum.get());
        pInfo.setDoingNum(doingNum.get());
        pInfo.setNotStartedNum(notStartedNum.get());
        pInfo.setRageOfDoneNum(numWith2Decimal((double) doneNum.get()*100/stuMap.size()));
        resList.add(pInfo);

        // 实验的完成率 完成人数 进行中人数  完成：3阶段全部完成
        Map<Long, List<TaskStepDto>> taskMap = taskStepDtoList.stream().collect(Collectors.groupingBy(TaskStepDto::getPtId));
        taskMap.forEach((ptId, taskDtoList)->{
            // 每个实验的数据
            Map<Long, List<TaskStepDto>> taskStuMap = taskDtoList
                    .stream()
                    .collect(Collectors.groupingBy(TaskStepDto::getPsId));
            AtomicInteger taskDoneNum = new AtomicInteger();
            AtomicInteger taskDoingNum = new AtomicInteger();
            AtomicInteger taskNotStartedNum = new AtomicInteger();
            taskStuMap.forEach((psId, dtoList)->{
                // 每个学生的数据
                int res = dtoList.stream().mapToInt(TaskStepDto::getBlockStatus).sum();
                if(res==0){
                    taskNotStartedNum.getAndIncrement();
                }else if(res>=1 && res<dtoList.size()){
                    taskDoingNum.getAndIncrement();
                }else if(res==dtoList.size()){
                    taskDoneNum.getAndIncrement();
                }
            });
            MonitorInfoVo.TaskInfo taskInfo = new MonitorInfoVo.TaskInfo();
            taskInfo.setTaskName(taskDtoList.get(0).getPtName());
            taskInfo.setIsProject(false);
            taskInfo.setPtId(ptId);
            taskInfo.setDoneNum(taskDoneNum.get());
            taskInfo.setDoingNum(taskDoingNum.get());
            taskInfo.setNotStartedNum(taskNotStartedNum.get());
            taskInfo.setRageOfDoneNum(numWith2Decimal((double) taskDoneNum.get()*100/taskStuMap.size()));
            resList.add(taskInfo);
        });
        return resList;
    }


    private static double numWith2Decimal(double num){
        return BigDecimal.valueOf(num)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
