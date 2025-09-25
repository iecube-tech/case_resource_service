package com.iecube.community.model.EMDV4Project.EMDV4_component.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.*;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.entity.EMDV4TaskGroup;
import com.iecube.community.model.EMDV4Project.EMDV4TaskGroup.service.EMDV4TaskGroupService;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.mapper.EMDV4ComponentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_component.service.EMDV4ComponentService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.mapper.EMDV4ProjectStudentMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import com.iecube.community.model.auth.service.ex.UpdateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EMDV4ComponentServiceImpl implements EMDV4ComponentService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EMDV4ComponentMapper emdV4ComponentMapper;

    @Autowired
    private EMDV4ProjectStudentTaskService emdv4ProjectStudentTaskService;

    @Autowired
    private EMDV4ProjectStudentMapper emdv4ProjectStudentMapper;

    @Autowired
    private EMDV4TaskGroupService emdv4TaskGroupService;

    @Autowired
    private EMDV4StudentTaskBookService taskBookService;

    @Override
    public EMDV4Component updateStatus(String id, int status) {
        int res = emdV4ComponentMapper.updateStatus(id, status);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        EMDV4Component res2 = emdV4ComponentMapper.getById(id);
        // 新增事件去 异步 处理批量更新 和 时间问题
        eventPublisher.publishEvent(new ComponentStatusChanged(this, res2));
        this.batchUpdateStatus(res2);
        return res2;
    }

    @Override
    public EMDV4Component updatePayload(String id, String payload) {
        int res = emdV4ComponentMapper.updatePayload(id, payload);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        EMDV4Component res2 = emdV4ComponentMapper.getById(id);
//        this.batchUpdatePayload(res2);
        eventPublisher.publishEvent(new ComponentPayloadChanged(this, res2));
        return res2;
    }

    @Override
    public EMDV4Component updateScore(String id, double score) {
        EMDV4Component oldComponent = emdV4ComponentMapper.getById(id);
        Double totalScore = oldComponent.getTotalScore();
        double per =0.0;
        if(totalScore==null || totalScore==0){
            score=0;
        }else {
            if(score>totalScore){
//                throw new UpdateException("成绩大于总成绩");
                score = totalScore;
            }
            per = (score / totalScore) *100;
        }
        int res = emdV4ComponentMapper.updateAiScore(id,score,per);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        emdv4ProjectStudentTaskService.computeAiScore(oldComponent.getBlockId()); // 计算本人的成绩
        EMDV4Component res1 = emdV4ComponentMapper.getById(id);
        eventPublisher.publishEvent(new ComponentAiScoreChanged(this, res1));
        return res1;
    }

    @Override
    public EMDV4Component checkScore(String id, double score){
        EMDV4Component oldComponent = emdV4ComponentMapper.getById(id);
        Double totalScore = oldComponent.getTotalScore();
        double per =0.0;
        if(totalScore==null || totalScore==0){
            score=0;
        }else {
            if(score>totalScore){
                throw new UpdateException("成绩大于总成绩");
            }
            per = (score / totalScore) *100;
        }
        int res = emdV4ComponentMapper.updateTScore(id,score,per);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }

        emdv4ProjectStudentTaskService.computeCheckScore(oldComponent.getBlockId()); // 计算本人的成绩
        EMDV4Component res1 = emdV4ComponentMapper.getById(id);
        eventPublisher.publishEvent(new ComponentTScoreChanged(this, res1));
        return res1;
    }

    @Override
    public void batchUpdateStatus(EMDV4Component component){
        List<EMDV4Component> groupWillUpdate = this.getWillUpdateComponents(component);
        if(groupWillUpdate==null){
            return;
        }
        groupWillUpdate.forEach(com->{
            com.setStatus(component.getStatus());
        });
        emdV4ComponentMapper.batchUpdateStatus(groupWillUpdate);
    }

    @Override
    public void batchUpdatePayload(EMDV4Component component){
        List<EMDV4Component> groupWillUpdate = this.getWillUpdateComponents(component);
        if(groupWillUpdate==null){
            return;
        }
        groupWillUpdate.forEach(com->{
            com.setPayload(component.getPayload());
        });
        emdV4ComponentMapper.batchUpdatePayload(groupWillUpdate);
    }

    @Override
    public void batchUpdateAiScore(EMDV4Component component){
        List<EMDV4Component> groupWillUpdate = this.getWillUpdateComponents(component);
        if(groupWillUpdate==null){
            return;
        }
        groupWillUpdate.forEach(com->{
            com.setScore(component.getScore());
            com.setAiScore(component.getAiScore());
            com.setTScore(component.getTScore());
            com.setScoreProportion(component.getScoreProportion());

        });
        emdV4ComponentMapper.batchUpdateAiScore(groupWillUpdate);

        // 需要触发计算成绩
        groupWillUpdate.forEach(component1 -> {
            eventPublisher.publishEvent(new ComputeAiScore(this,component1));
        });
    }

    @Override
    public void batchUpdateTScore(EMDV4Component component){
        List<EMDV4Component> groupWillUpdate = this.getWillUpdateComponents(component);
        if(groupWillUpdate==null){
            return;
        }
        groupWillUpdate.forEach(com->{
            com.setTScore(component.getTScore());
            com.setScore(component.getScore());
            com.setScoreProportion(component.getScoreProportion());
        });
        emdV4ComponentMapper.batchUpdateTScore(groupWillUpdate);
        // 需要触发计算成绩

        // 需要触发计算成绩
        groupWillUpdate.forEach(component1 -> {
            eventPublisher.publishEvent(new ComputeTScore(this,component1));
        });
    }

    /**
     * 获取需要同步的component列表
     * @param changedComponent 已更改的component
     * @return 需要更改component的列表
     */
    private List<EMDV4Component> getWillUpdateComponents(EMDV4Component changedComponent){
        EMDV4ProjectStudentTask PST = emdv4ProjectStudentTaskService.getPSTByComponent(changedComponent);
        EMDV4ProjectStudent PS = emdv4ProjectStudentMapper.getById(PST.getProjectStudent());

        // 3. 查询PST的分组
        EMDV4TaskGroup studentGroup = emdv4TaskGroupService.getTaskStudentGroup(PST.getProjectTask(), PS.getStudentId());
        if(studentGroup==null){
            return null;
        }
        List<Integer> stuIdList = new ArrayList<>();
        studentGroup.getStudentList().forEach(student -> {
            stuIdList.add(student.getId());
        });
        // 判断小组人数，需不需要同步
        if(stuIdList.size()<=1){
            return null;
        }

        // 4. 获取小组的PST列表
        List<EMDV4ProjectStudentTask> groupPSTList = emdv4ProjectStudentTaskService.getListByPTAndStu(PST.getProjectTask(), stuIdList);
        // 分解实验指导书，递归获取叶子节点的block 放到列表 根据当前的block找到目标block
        List<EMDV4StudentTaskBook> treeList = new ArrayList<>();
        groupPSTList.forEach(pst->{
            treeList.add(pst.getStudentTaskBook());
        });
        Map<String,List<EMDV4StudentTaskBook>> leafNodeMap = new HashMap<>();
        for(EMDV4StudentTaskBook tree: treeList){
            //递归获取所有叶子节点
            List<EMDV4StudentTaskBook> leafNodes = taskBookService.getAllLeafNodes(tree);
            leafNodeMap.put(tree.getId(), leafNodes);
        }
        List<EMDV4StudentTaskBook> changedPSTBlockLeafs = leafNodeMap.get(PST.getTaskBookId());
        EMDV4StudentTaskBook changedLeaf = null; // 当前更改的block是这个
        for (EMDV4StudentTaskBook leaf : changedPSTBlockLeafs) {
            if (leaf.getId().equals(changedComponent.getBlockId())) {
                changedLeaf = leaf;
            }
        }

        if(changedLeaf==null){
            throw new UpdateException("同步小组数据异常");
        }

        List<String> willUpdateBlockIds = new ArrayList<>(); //需要更改的blockId 作为Component的BlockId

        for(EMDV4ProjectStudentTask pst : groupPSTList){
            if (pst.getId().equals(PST.getId())){
                continue;
            }
            // 找出需要更改的block
            List<EMDV4StudentTaskBook> pstLeafs = leafNodeMap.get(pst.getTaskBookId());
            for(EMDV4StudentTaskBook pstLeaf: pstLeafs){
                if(pstLeaf.getLevel().equals(changedLeaf.getLevel())
                        && pstLeaf.getOrder().equals(changedLeaf.getOrder())
                        && pstLeaf.getName().equals(changedLeaf.getName())
                        && pstLeaf.getType().equals(changedLeaf.getType())
                ){
                    willUpdateBlockIds.add(pstLeaf.getId());
                }
            }
        }

        // 返回需要更改的component
        return emdV4ComponentMapper.batchGetByOrderAndBlockIds(changedComponent.getOrder(),
                changedComponent.getName(),
                changedComponent.getType(),
                willUpdateBlockIds);
    }
}
