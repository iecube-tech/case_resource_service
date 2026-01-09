package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.impl;

import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.MonitorStatus;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.StuMonitor;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.TaskStepDto;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.mapper.EMDV4ProjectMonitorMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.EMDV4MonitorService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorOverview;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorStuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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

    // 课程栏目下学生列表
    @Override
    public MonitorOverview getStuMonitorPaging(Integer projectId, Integer page, Integer pageSize) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(page <1 || pageSize<1){
            page = 1;
            pageSize=10;
        }

        int offset = (page - 1) * pageSize;
        List<StuMonitor> pagingStuMonitors = monitorMapper.getStuMonitorPaging(projectId, offset, pageSize);
        MonitorOverview res = this.genCourseStuView(pagingStuMonitors);
        res.setTotal(monitorMapper.getTotalStuNum(projectId));
        return res;
    }

    // 状态筛选后课程栏目下学生列表
    public MonitorOverview getStuMonitorPagingWithStatus(Integer projectId, Integer page, Integer pageSize, Integer status) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(page <1 || pageSize<1){
            page = 1;
            pageSize=10;
        }
        if(status==null){
            return this.getStuMonitorPaging(projectId, page, pageSize);
        }
        List<Integer> targetStatus = new ArrayList<>();
        for(MonitorStatus s : MonitorStatus.values()){
            targetStatus.add(s.getStatus());
        }
        if( !targetStatus.contains(status)){
            return this.getStuMonitorPaging(projectId, page, pageSize);
        }
        List<StuMonitor> allLevel12 = monitorMapper.getStuMonitorAll(projectId);
        List<Long> notStart = new ArrayList<>();
        List<Long> doing = new ArrayList<>();
        List<Long> done = new ArrayList<>();
        Map<Long, List<StuMonitor>> groupByStu = allLevel12.stream()
                .collect(Collectors.groupingBy(StuMonitor::getPsId));
        groupByStu.forEach((psId, taskList)->{
            List<Integer> taskStatus = new ArrayList<>(); // 0 1 2
            taskList.stream()
                    .collect(Collectors.groupingBy(StuMonitor::getPtId))
                    .forEach((ptId, list)->{
                        // 判断每个实验的完成情况
                        List<Integer> stageStatus = new ArrayList<>(); // 0 1 2
                        list.stream().filter(m->m.getBlockLevel().equals(2)).forEach(m->{
                            if(m.getBlockStatus().equals(0)){
                                if(m.getBlockStartTime()==null){
                                    // 未开始
                                    stageStatus.add(0);
                                }else {
                                    // 进行中
                                    stageStatus.add(1);
                                }
                            }else {
                                // 已完成
                                stageStatus.add(2);
                            }
                        });
                        // 判断 task 的 stageStatus 全为0未开始  全为2 已完成
                        if(stageStatus.stream().mapToInt(Integer::intValue).sum()==0){
                            // 未开始
                            taskStatus.add(0);
                        } else if (stageStatus.stream().mapToInt(Integer::intValue).sum()==2*stageStatus.size()) {
                            taskStatus.add(2);
                        }else {
                            taskStatus.add(1);
                        }
                    });
            if(taskStatus.stream().mapToInt(Integer::intValue).sum()==0){
                // 课程未开始
                notStart.add(psId);
            }else if (taskStatus.stream().mapToInt(Integer::intValue).sum()==2*taskStatus.size()) {
                // 课程已完成
                done.add(psId);
            }else{
                // 课程进行中
                doing.add(psId);
            }
        });
        List<StuMonitor> targetList = new ArrayList<>();
        int total = 0;
        if(status.equals(MonitorStatus.NOTSTART.getStatus())){
            total=notStart.size();
            notStart.sort(Comparator.comparingLong(Long::longValue));
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i<=notStart.size()-1){
                    targetList.addAll(groupByStu.get(notStart.get(offset+i)));
                }
            }
        } else if (status.equals(MonitorStatus.DOING.getStatus())) {
            total=doing.size();
            doing.sort(Comparator.comparingLong(Long::longValue));
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i<=doing.size()-1){
                    targetList.addAll(groupByStu.get(doing.get(offset+i)));
                }
            }
        } else if (status.equals(MonitorStatus.DONE.getStatus())) {
            total=done.size();
            done.sort(Comparator.comparingLong(Long::longValue));
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i<=done.size()-1){
                    targetList.addAll(groupByStu.get(done.get(offset+i)));
                }
            }
        }else {
            return new MonitorOverview();
        }
        MonitorOverview res = this.genCourseStuView(targetList);
        res.setTotal(total);
        return res;
    }


    // 单个实验栏目下学生列表
    @Override
    public MonitorOverview getTaskStuMonitorPaging(Integer projectId, Long ptId, Integer page, Integer pageSize) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(page <1 || pageSize<1){
            page = 1;
            pageSize=10;
        }
        int offset = (page - 1) * pageSize;
        List<StuMonitor> ptStuMonitors = monitorMapper.getTaskStuMonitorPaging(projectId, ptId, offset, pageSize);
        MonitorOverview res = this.genTaskStuView(ptStuMonitors);
        res.setTotal(monitorMapper.getTotalStuNum(projectId));
        return res;
    }

    // 状态筛选后 单个实验栏目下学生列表
    public MonitorOverview getTaskStuMonitorPagingWithStatus(Integer projectId, Long ptId, Integer page, Integer pageSize, Integer status) {
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(page <1 || pageSize<1){
            page = 1;
            pageSize=10;
        }
        if(status==null){
            return this.getTaskStuMonitorPaging(projectId, ptId, page, pageSize);
        }
        List<Integer> targetStatus = new ArrayList<>();
        for(MonitorStatus s : MonitorStatus.values()){
            targetStatus.add(s.getStatus());
        }
        if( !targetStatus.contains(status)){
            return this.getTaskStuMonitorPaging(projectId, ptId, page, pageSize);
        }
        List<StuMonitor> thisTaskAllStuMonitorList = monitorMapper.getTaskStuMonitorAll(projectId, ptId);

        Map<Long, List<StuMonitor>> groupByStu = thisTaskAllStuMonitorList.stream().distinct().collect(Collectors.groupingBy(StuMonitor::getPsId));
        List<Long> notstart = new ArrayList<>();
        List<Long> done = new ArrayList<>();
        List<Long> doing = new ArrayList<>();

        groupByStu.forEach((psId, psMonitorList)->{
            // 判断学生的实验是否已完成 看level=2 的状态
            List<Integer> level2Status = new ArrayList<>();
            psMonitorList.stream().filter(m->m.getBlockLevel().equals(2)).forEach(m->{
                if(m.getBlockStatus().equals(0)){
                    if(m.getBlockStartTime()==null){
                        level2Status.add(0);
                    }else {
                        level2Status.add(1);
                    }
                }else {
                    level2Status.add(2);
                }
            });
            if(level2Status.stream().mapToInt(Integer::intValue).sum()==0){
                // 未开始
                notstart.add(psId);
            } else if (level2Status.stream().mapToInt(Integer::intValue).sum()==2*level2Status.size()) {
                // 已完成
                done.add(psId);
            } else {
                // 进行中
                doing.add(psId);
            }
        });
        List<StuMonitor> targetList = new ArrayList<>();
        int total = 0;
        if(status.equals(MonitorStatus.NOTSTART.getStatus())){
            notstart.sort(Comparator.comparingLong(Long::longValue));
            total=notstart.size();
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i<=notstart.size()-1){
                    targetList.addAll(groupByStu.get(notstart.get(offset+i)));
                }
            }
        } else if (status.equals(MonitorStatus.DOING.getStatus())) {
            doing.sort(Comparator.comparingLong(Long::longValue));
            total=doing.size();
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i <= doing.size()-1){
                    targetList.addAll(groupByStu.get(doing.get(offset+i)));
                }
            }
        } else if (status.equals(MonitorStatus.DONE.getStatus())) {
            done.sort(Comparator.comparingLong(Long::longValue));
            total=done.size();
            int offset = (page - 1) * pageSize;
            for(int i=0; i<pageSize; i++){
                if(offset+i <= done.size()-1){
                    targetList.addAll(groupByStu.get(done.get(offset+i)));
                }
            }
        }else {
            return new MonitorOverview();
        }
        MonitorOverview res = this.genCourseStuView(targetList);
        res.setTotal(total);
        return res;
    }

    public MonitorOverview searchStudent(Integer projectId, Long ptId, Integer page, Integer pageSize, String keyword){
        if(page==null){
            page=1;
        }
        if(pageSize==null){
            pageSize=10;
        }
        if(page <1 || pageSize<1){
            page = 1;
            pageSize=10;
        }
        List<StuMonitor> stuMonitorList =
                ptId == null ? monitorMapper.studentSearch(projectId, keyword): monitorMapper.taskStudentSearch(projectId, ptId, keyword);
        if(stuMonitorList.isEmpty()){
            return new MonitorOverview();
        }
        Map<Long, List<StuMonitor>> groupByPsId = stuMonitorList.stream().collect(Collectors.groupingBy(StuMonitor::getPsId));
        List<Long>  psList = groupByPsId.keySet().stream().sorted(Comparator.comparing(Long::longValue)).toList();
        List<StuMonitor> targetList = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        for(int i=0; i<pageSize; i++){
            if(offset+i <= psList.size()-1){
                targetList.addAll(groupByPsId.get(psList.get(i)));
            }
        }
        MonitorOverview res = ptId == null ? this.genCourseStuView(targetList) : this.genTaskStuView(targetList);
        res.setTotal(psList.size());
        return res;
    }

    @Override
    public MonitorStuVo getStuTaskMonitor(Integer projectId, Long psId){
        List<StuMonitor> stuMonitorList = monitorMapper.getStuTaskMonitor(projectId, psId);
        return this.genMonitorStuVo(stuMonitorList);
    }

    private MonitorOverview genCourseStuView(List<StuMonitor> monitorList){
        if(monitorList.isEmpty()){
            return new MonitorOverview();
        }

        MonitorOverview monitorOverview = new MonitorOverview();
        List<MonitorOverview.taskInfo> tasks = new ArrayList<>();
        List<MonitorOverview.monitorOverview> stuMonitors = new ArrayList<>();

        monitorList.stream().collect(Collectors.groupingBy(StuMonitor::getPtId)).forEach((ptId, list)->{
            MonitorOverview.taskInfo taskInfo = new MonitorOverview.taskInfo();
            taskInfo.setPtId(ptId);
            taskInfo.setPtName(list.get(0).getPtName());
            tasks.add(taskInfo);
        });
        tasks.sort(Comparator.comparingLong(MonitorOverview.taskInfo::getPtId));
        monitorOverview.setTasks(tasks);

        Map<Long, List<StuMonitor>> groupByPs = monitorList
                .stream()
                .collect(Collectors.groupingBy(StuMonitor::getPsId));

        groupByPs.forEach((psId, stuList)->{  // 学生列表
            Map<Long, List<StuMonitor>> groupByPst = stuList
                    .stream()
                    .collect(Collectors.groupingBy(StuMonitor::getPtId));
            List<MonitorOverview.taskInfo> taskInfos = new ArrayList<>();

            groupByPst.forEach((ptId, taskList)->{ // 每个学生的实验列表
                MonitorOverview.taskInfo taskInfo = new MonitorOverview.taskInfo();
                taskInfo.setPstId(taskList.get(0).getPstId());
                taskInfo.setPtId(ptId);
                taskInfo.setPtName(taskList.get(0).getPtName());
                List<StuMonitor> level0 = taskList.stream().filter(t->t.getBlockLevel().equals(1)).toList();
                if(!level0.isEmpty()){
                    taskInfo.setPtScore(level0.get(0).getBlockScore());
                    taskInfo.setPtTotalScore(level0.get(0).getBlockTotalScore());
                }

                List<MonitorOverview.taskStage> taskStages = new ArrayList<>();
                List<StuMonitor> level1 = taskList
                        .stream()
                        .filter(t->t.getBlockLevel().equals(2))
                        .toList();
                if(!level1.isEmpty()){
                    level1.forEach(t->{
                        MonitorOverview.taskStage taskStage = new MonitorOverview.taskStage();
                        taskStage.setStage(t.getBlockStage());
                        taskStage.setStageName(t.getBlockName());
                        if(t.getBlockStatus().equals(0)){
                            if (t.getBlockStartTime()!=null){
                                taskStage.setStageStatus(MonitorStatus.DOING);
                            }else{
                                taskStage.setStageStatus(MonitorStatus.NOTSTART);
                            }
                        }else {
                            taskStage.setStageStatus(MonitorStatus.DONE);
                        }
                        taskStages.add(taskStage);
                    });
                }
                taskInfo.setStageList(taskStages);
                taskInfos.add(taskInfo);
            });

            MonitorOverview.monitorOverview monitor = new MonitorOverview.monitorOverview();
            monitor.setPsId(psId);
            monitor.setStuId(stuList.get(0).getStuId());
            monitor.setStuName(stuList.get(0).getStuName());
            monitor.setPsScore(stuList.get(0).getPsScore());
            taskInfos.sort(Comparator.comparingLong(MonitorOverview.taskInfo::getPtId));
            monitor.setTasks(taskInfos);

            stuMonitors.add(monitor);
        });
        stuMonitors.sort(Comparator.comparingLong(MonitorOverview.monitorOverview::getPsId));
        monitorOverview.setStuMonitors(stuMonitors);
        return monitorOverview;
    }

    private MonitorOverview genTaskStuView(List<StuMonitor> monitorList){
        if(monitorList.isEmpty()){
            return new MonitorOverview();
        }
        Map<Long, List<StuMonitor>> stuPtMonitors = monitorList
                .stream()
                .collect(Collectors.groupingBy(StuMonitor::getPsId));
        MonitorOverview monitorOverview = new MonitorOverview();

        List<MonitorOverview.monitorOverview> stuMonitors = new ArrayList<>();

        stuPtMonitors.forEach((psId, stuList)->{
            MonitorOverview.monitorOverview monitor = new MonitorOverview.monitorOverview();
            monitor.setPsId(psId);
            monitor.setStuId(stuList.get(0).getStuId());
            monitor.setStuName(stuList.get(0).getStuName());
            MonitorOverview.taskInfo pstInfo = new MonitorOverview.taskInfo();
            pstInfo.setPstId(stuList.get(0).getPstId());
            pstInfo.setPtId(stuList.get(0).getPtId());
            pstInfo.setPtName(stuList.get(0).getPtName());
            List<StuMonitor> level1 = stuList.stream().filter(t->t.getBlockLevel().equals(1)).toList();
            if(!level1.isEmpty()){ // level=1取值
                pstInfo.setPtScore(level1.get(0).getBlockScore()); // level=1取值
                pstInfo.setPtTotalScore(level1.get(0).getBlockTotalScore()); // level=1取值
                if(level1.get(0).getBlockStatus().equals(0)){
                    if(level1.get(0).getBlockStartTime()!=null){
                        pstInfo.setStatus(MonitorStatus.DOING);
                    }else {
                        pstInfo.setStatus(MonitorStatus.NOTSTART);
                    }
                }else {
                    pstInfo.setStatus(MonitorStatus.DONE);
                }

            }

            List<MonitorOverview.taskStage> stageList = new ArrayList<>();
            stuList.stream()
                    .filter(m-> !m.getBlockLevel().equals(1) && !m.getBlockLevel().equals(2))
                    .collect(Collectors.groupingBy(StuMonitor::getBlockStage))
                    .forEach((stage,list)->{
                        List<MonitorOverview.stageBlock> stageBlockList = new ArrayList<>();
                        list.forEach(m->{
                            MonitorOverview.stageBlock stageBlock = new MonitorOverview.stageBlock();
                            stageBlock.setBlockName(m.getBlockName());
                            stageBlock.setBlockOrder(m.getBlockOrder());
                            stageBlock.setBlockStartTime(m.getBlockStartTime());
                            stageBlock.setBlockEndTime(m.getBlockEndTime());
                            if(m.getBlockStartTime()!=null && m.getBlockEndTime()!=null){
                                long diffMs = m.getBlockEndTime().getTime() - m.getBlockStartTime().getTime();
                                long diffMinutes = diffMs / (60 * 1000);
                                stageBlock.setMinutesDiff((int)diffMinutes);
                            }
                            if(m.getBlockStatus().equals(0)){
                                if(m.getBlockStartTime()==null){
                                    stageBlock.setStatus(MonitorStatus.NOTSTART);
                                }
                                else {
                                    stageBlock.setStatus(MonitorStatus.DOING);
                                }
                            }else {
                                stageBlock.setStatus(MonitorStatus.DONE);
                            }
                            stageBlockList.add(stageBlock);
                        });
                        stageBlockList.sort(Comparator.comparingInt(MonitorOverview.stageBlock::getBlockOrder));
                        MonitorOverview.taskStage taskStage = new MonitorOverview.taskStage();
                        List<StuMonitor> thisStageLevel2 = stuList
                                .stream()
                                .filter(sm->sm.getBlockStage().equals(stage)&&sm.getBlockLevel().equals(2))
                                .toList();
                        taskStage.setStage(stage);
                        if(!thisStageLevel2.isEmpty()){
                            taskStage.setStageName(thisStageLevel2.get(0).getBlockName());
                            if(thisStageLevel2.get(0).getBlockStatus().equals(0)){
                                if(thisStageLevel2.get(0).getBlockStartTime()==null){
                                    taskStage.setStageStatus(MonitorStatus.NOTSTART);
                                }else {
                                    taskStage.setStageStatus(MonitorStatus.DOING);
                                }
                            }
                            else {
                                taskStage.setStageStatus(MonitorStatus.DONE);
                            }
                        }
                        taskStage.setStageBlockList(stageBlockList);
                        stageList.add(taskStage);
                    });
            stageList.sort(Comparator.comparingLong(MonitorOverview.taskStage::getStage));
            pstInfo.setStageList(stageList);

            monitor.setPstInfo(pstInfo);
            stuMonitors.add(monitor);

        });
        stuMonitors.sort(Comparator.comparingLong(MonitorOverview.monitorOverview::getPsId));
        monitorOverview.setStuMonitors(stuMonitors);

        MonitorOverview.taskInfo ptInfo = new MonitorOverview.taskInfo();
        List<MonitorOverview.taskStage> stageList = new ArrayList<>();
        List<StuMonitor> ptStageList = monitorList.stream() // 取叶子节点 blockName 构造pt结构
                .filter(m->m.getPsId().equals(monitorList.get(0).getPsId()))
                .filter(m->!m.getBlockLevel().equals(1) && !m.getBlockLevel().equals(2))
                .toList();
        List<StuMonitor> ptStageLevel2List = monitorList.stream() // 取level为2的 获取步骤信息
                .filter(m->m.getPsId().equals(monitorList.get(0).getPsId()))
                .filter(m-> m.getBlockLevel().equals(2))
                .toList();

        ptStageList.stream()
                .collect(Collectors.groupingBy(StuMonitor::getBlockStage))
                .forEach((stage,list)->{
                    List<MonitorOverview.stageBlock> stageBlockList = new ArrayList<>();
                    list.forEach(m->{
                        MonitorOverview.stageBlock stageBlock = new MonitorOverview.stageBlock();
                        stageBlock.setBlockName(m.getBlockName());
                        stageBlock.setBlockOrder(m.getBlockOrder());
                        stageBlockList.add(stageBlock);
                        stageBlockList.sort(Comparator.comparingInt(MonitorOverview.stageBlock::getBlockOrder));
                    });
                    MonitorOverview.taskStage taskStage = new MonitorOverview.taskStage();
                    taskStage.setStage(stage);
                    if(!ptStageLevel2List.isEmpty()){
                        List<StuMonitor> thisStage = ptStageLevel2List.stream().filter(sm->sm.getBlockStage().equals(stage)).toList();
                        if(!thisStage.isEmpty()){
                            taskStage.setStageName(thisStage.get(0).getBlockName());
                        }
                    }
                    taskStage.setStageBlockList(stageBlockList);
                    stageList.add(taskStage);
                });
        stageList.sort(Comparator.comparingInt(MonitorOverview.taskStage::getStage));
        ptInfo.setPtId(monitorList.get(0).getPtId());
        ptInfo.setPtName(monitorList.get(0).getPtName());
        ptInfo.setStageList(stageList);
        monitorOverview.setTask(ptInfo);

        return monitorOverview;  // TODO 返回的数据中每个叶子节点的时间为空，由于实验过程中未精确记录到叶子节点的时间，暂时为空 优化实验过程中时间记录

    }

    private MonitorStuVo genMonitorStuVo(List<StuMonitor> stuMonitorList){
        if(stuMonitorList.isEmpty()){
            return null;
        }
        List<Date> dateList = new ArrayList<>();
        stuMonitorList.stream().distinct().forEach(m->{
            if(m.getBlockStartTime()!=null){
                dateList.add(m.getBlockStartTime());
            }
            if(m.getBlockEndTime()!=null){
                dateList.add(m.getBlockEndTime());
            }
        });
        dateList.sort(Comparator.comparingLong(Date::getTime));
        Map<Long, List<StuMonitor>> ptMap = stuMonitorList
                .stream()
                .distinct()
                .collect(Collectors.groupingBy(StuMonitor::getPtId));
        MonitorStuVo stuVo = new MonitorStuVo();
        stuVo.setPsId(stuMonitorList.get(0).getPsId());
        stuVo.setStuName(stuMonitorList.get(0).getStuName());
        stuVo.setStuId(stuMonitorList.get(0).getStuId());
        if(!dateList.isEmpty()){
            stuVo.setLastOperateTime(dateList.get(dateList.size()-1));
        }
        List<MonitorStuVo.PSTInfo> taskList = new ArrayList<>();
        ptMap.forEach((ptId, smList)->{
            List<StuMonitor> level1 = smList.stream().filter(m->m.getBlockLevel().equals(1)).toList();
            MonitorStuVo.PSTInfo pstInfo = new MonitorStuVo.PSTInfo();
            pstInfo.setPtId(ptId);
            pstInfo.setPtName(smList.get(0).getPtName());
            if(!level1.isEmpty()){
                if(level1.get(0).getBlockStatus().equals(0)){
                    if(level1.get(0).getBlockStartTime()==null){
                        pstInfo.setStatus(MonitorStatus.NOTSTART);
                    }
                    else {
                        pstInfo.setStatus(MonitorStatus.DOING);
                    }
                }else {
                    pstInfo.setStatus(MonitorStatus.DONE);
                }
                pstInfo.setPtScore(level1.get(0).getBlockScore());
                pstInfo.setPtTotalScore(level1.get(0).getBlockTotalScore());
                List<MonitorStuVo.Block> stageList = new ArrayList<>();
                smList.stream()
                        .filter(m->m.getBlockLevel().equals(2))
                        .collect(Collectors.groupingBy(StuMonitor::getBlockStage))
                        .forEach((stage, level2List)->{
                            MonitorStuVo.Block stageNode = new MonitorStuVo.Block();
                            stageNode.setBlockName(level2List.get(0).getBlockName());
                            stageNode.setBlockOrder(level2List.get(0).getBlockOrder());
                            stageNode.setBlockStartTime(level2List.get(0).getBlockStartTime());
                            stageNode.setBlockEndTime(level2List.get(0).getBlockEndTime());
                            if(level2List.get(0).getBlockStatus().equals(0)){
                                if(level2List.get(0).getBlockStartTime()==null){
                                    stageNode.setStatus(MonitorStatus.NOTSTART);
                                }else {
                                    stageNode.setStatus(MonitorStatus.DOING);
                                }
                            }else {
                                stageNode.setStatus(MonitorStatus.DONE);
                            }
                            if(level2List.get(0).getBlockStartTime()!=null&&level2List.get(0).getBlockEndTime()!=null){
                                long diffs = level2List.get(0).getBlockEndTime().getTime() - level2List.get(0).getBlockStartTime().getTime();
                                long diffMinutes = diffs/(60*1000);
                                stageNode.setMinutesDiff((int)diffMinutes);
                            }
                            List<MonitorStuVo.Block> blockList = new ArrayList<>();
                            smList.stream()
                                    .distinct()
                                    .filter(m->!m.getBlockLevel().equals(1) && !m.getBlockLevel().equals(2) && m.getBlockStage().equals(stage))
                                    .forEach(block->{
                                        MonitorStuVo.Block blockNode = new MonitorStuVo.Block();
                                        blockNode.setBlockName(block.getBlockName());
                                        blockNode.setBlockOrder(block.getBlockOrder());
                                        blockNode.setBlockStartTime(block.getBlockStartTime());
                                        blockNode.setBlockEndTime(block.getBlockEndTime());
                                        if(block.getBlockStatus().equals(0)){
                                            if(block.getBlockStartTime()==null){
                                                blockNode.setStatus(MonitorStatus.NOTSTART);
                                            }
                                            else {
                                                blockNode.setStatus(MonitorStatus.DOING);
                                            }
                                        }else {
                                            blockNode.setStatus(MonitorStatus.DONE);
                                        }
                                        if(block.getBlockStartTime()!=null && block.getBlockEndTime()!=null){
                                            long diffs = block.getBlockEndTime().getTime() - block.getBlockStartTime().getTime();
                                            long diffMinutes = diffs/(60*1000);
                                            blockNode.setMinutesDiff((int)diffMinutes);
                                        }
                                        blockList.add(blockNode);
                                    });
                            blockList.sort(Comparator.comparingInt(MonitorStuVo.Block::getBlockOrder));
                            stageNode.setBlockList(blockList);
                            stageList.add(stageNode);
                        });
                stageList.sort(Comparator.comparingInt(MonitorStuVo.Block::getBlockOrder));
                pstInfo.setStageList(stageList);
            }
            taskList.add(pstInfo);
        });

        taskList.sort(Comparator.comparingLong(MonitorStuVo.PSTInfo::getPtId));
        stuVo.setTaskList(taskList);
        return stuVo;
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



















