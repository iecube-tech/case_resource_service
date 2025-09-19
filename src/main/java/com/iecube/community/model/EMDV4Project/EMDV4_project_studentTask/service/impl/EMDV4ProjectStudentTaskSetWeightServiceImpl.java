package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.impl;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper.EMDV4ProjectStudentTaskMapper;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskSetWeightService;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EMDV4ProjectStudentTaskSetWeightServiceImpl implements EMDV4ProjectStudentTaskSetWeightService {

    @Autowired
    private EMDV4ProjectStudentTaskMapper emdV4ProjectStudentTaskMapper;

    @Autowired
    private EMDV4StudentTaskBookService emdV4StudentTaskBookService;


    @Async
    @Override
    public void checkProjectTaskWeighting(List<EMDV4StudentTaskBook> targetBlockList, EMDV4ProjectStudentTask PST) {
        log.info("开始执行异步变更实验步骤的权重");
        // 获取所有需要更改权重的taskBook 结果为 该PST 对应的 projectTask中 level=2的bookLabList
        List<EMDV4StudentTaskBook> allBlockLevel2 = emdV4StudentTaskBookService.getProjectTaskBlockList(PST.getProjectTask());

        // 不需要再次更新的block的id， 也是获取 weighting值的block 的id
        List<String> tagetIdList = new ArrayList<>();
        for(EMDV4StudentTaskBook block : targetBlockList){
            tagetIdList.add(block.getId());
        }

        Map<Long, EMDV4StudentTaskBook> targetSourceMap = new HashMap<>(); // 从这个map中获取对应的weighting
        List<EMDV4StudentTaskBook> willUpdateBlockList = new ArrayList<>(); // 这个map存储需要更新weighting的block

        Long targetSourceId = null;

        for (EMDV4StudentTaskBook block : allBlockLevel2) {
            if(tagetIdList.contains(block.getId())){
                targetSourceMap.put(block.getSourceId(), block);
                targetSourceId = block.getSourceId();
            }else{
                willUpdateBlockList.add(block);
            }
        }

        if(willUpdateBlockList.isEmpty()){
            log.warn("没有别的学生需要更新实验步骤的权重");
            return;
        }

        willUpdateBlockList.forEach(block->{
            if(targetSourceMap.containsKey(block.getSourceId())){
                block.setWeighting(targetSourceMap.get(block.getSourceId()).getWeighting());
            }
        });

        // 更新了权重并返回了所有的level=2的更新权重后的数据
        List<EMDV4StudentTaskBook> taskBookList = emdV4StudentTaskBookService.batchUpdateWeighting(willUpdateBlockList);

        List<EMDV4StudentTaskBook> useComputeScoreBlockList  = new ArrayList<>(); // 用于计算成绩的子节点

        for(EMDV4StudentTaskBook block : taskBookList){   //从 所有的节点中调出sourceId相同即为不同树中的相同位置的节点
            if(block.getSourceId().equals(targetSourceId)){
                useComputeScoreBlockList.add(block);
            }
        }

        List<EMDV4StudentTaskBook> willUseUpPSTScore = new ArrayList<>();
        useComputeScoreBlockList.forEach(block->{
//            System.out.println("调用计算成绩"+ block.getPId());
            willUseUpPSTScore.add(emdV4StudentTaskBookService.computeTaskBookScore(block));
        });

        List<EMDV4ProjectStudentTask> PSTList = new ArrayList<>();
        willUseUpPSTScore.forEach(block->{
            EMDV4ProjectStudentTask pst = new EMDV4ProjectStudentTask();
            pst.setScore(block.getScore());
            pst.setTaskBookId(block.getId());
            PSTList.add(pst);
        });
        int res = emdV4ProjectStudentTaskMapper.batchUpdateScore(PSTList);
        System.out.println(res);
        if(allBlockLevel2.size()/targetSourceMap.size()-1 != res){
            log.error("异步变更实验步骤的权重并计算成绩执行错误，数量不符");
        }
        log.info("异步变更实验步骤的权重并计算成绩执行完成");
    }
}
