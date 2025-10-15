package com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.listener;

import com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event.*;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_component.service.EMDV4ComponentService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.service.EMDV4ProjectStudentTaskService;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service.EMDV4StudentTaskBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EMDV4PSTListener {

    private final EMDV4ComponentService componentService;
    private final EMDV4StudentTaskBookService taskBookService;
    private final EMDV4ProjectStudentTaskService projectStudentTaskService;

    @Autowired
    public EMDV4PSTListener(EMDV4ComponentService componentService,
                            EMDV4StudentTaskBookService taskBookService,
                            EMDV4ProjectStudentTaskService projectStudentTaskService) {
        this.componentService = componentService;
        this.taskBookService = taskBookService;
        this.projectStudentTaskService = projectStudentTaskService;
    }

    @Async
    @EventListener
    public void batchUpCompStatusEvent(ComponentStatusChanged event) {
        log.info("异步事件：ComponentStatusChanged - 同步小组");
        EMDV4Component changed = event.getComponent();
        if(isStep1(changed)){
            componentService.batchUpdateStatus(changed);
        }
    }

    @Async
    @EventListener
    @Order(1)
    public void batchUpCompPayloadEvent(ComponentPayloadChanged event) {
        log.info("异步事件：ComponentPayloadChanged - 同步小组");
        // 事件1 批量更新小组Payload内容
        EMDV4Component changed = event.getComponent();
        if(isStep1(changed)){
            componentService.batchUpdatePayload(changed);
        }
    }

    @Async
    @EventListener
    @Order(2)
    public void batchUpLevel2BlockTimeEvent(ComponentPayloadChanged event) {
        log.info("异步事件：ComponentPayloadChanged - 同步小组大步骤时间");
        // 事件2 更新大步骤的开始时间
        EMDV4Component changed = event.getComponent();
        taskBookService.handleUpdateLevel2StartTime(changed);
    }


    @Async
    @EventListener
    public void batchUpAiScoreEvent(ComponentAiScoreChanged event) {
        log.info("异步事件：ComponentAiScoreChanged - 计算成绩");
        // 批量更新小组内的 component 成绩并计算总成绩
        EMDV4Component changed = event.getComponent();
        if(isStep1(changed)){
            componentService.batchUpdateAiScore(changed);
        }
    }

    @Async
    @EventListener
    public void batchUpTScoreEvent(ComponentTScoreChanged event) {
        log.info("异步事件：ComponentTScoreChanged - 计算成绩");
        // 批量更新小组内的 component 成绩并计算总成绩
        EMDV4Component changed = event.getChanged();
        if(isStep1(changed)){
            componentService.batchUpdateTScore(changed);
        }
    }

    @Async
    @EventListener
    public void batchUpdateBlockStatus(BlockStatusChanged event){
        log.info("异步事件：BlockStatusChanged - 同步小组");
        EMDV4StudentTaskBook changed = event.getChangedBlock();
        if(isStep1(changed)){
            taskBookService.batchUpdateBlockStatus(changed);
        }
    }

    @Async
    @EventListener
    public void batchUpdateBlockCurrentChild(BlockCurrentChildChanged event){
        log.info("异步事件：BlockCurrentChildChanged - 同步小组");
        EMDV4StudentTaskBook changed = event.getChangedBlock();
        if(isStep1(changed)){
            taskBookService.batchUpdateBlockCurrentChild(changed);
        }
    }

    @Async
    @EventListener
    public void batchUpdateBlockScoreChanged(BlockScoreChanged event){
        log.info("异步事件：BlockScoreChanged - 同步小组");
        EMDV4StudentTaskBook changed = event.getChangedBlock();
        if(isStep1(changed)){
            taskBookService.batchUpdateBlockScore(changed);
        }
    }

    @Async
    @EventListener
    public void batchUpdateBlockTime(BlockTimeChanged event){
        log.info("异步事件：BlockTimeChanged - 同步小组");
        EMDV4StudentTaskBook changed = event.getChangedBlock();
        if(isStep1(changed)){
            taskBookService.batchUpdateBlockTime(changed);
        }
    }

    @Async
    @EventListener
    public void handleComputeAiScore(ComputeAiScore event){
        EMDV4Component changed = event.getChanged();
        projectStudentTaskService.computeAiScore(changed.getBlockId());
    }

    @Async
    @EventListener
    public void handleComputeTScore(ComputeTScore event){
        EMDV4Component changed = event.getChanged();
        projectStudentTaskService.computeCheckScore(changed.getBlockId());
    }

    private Boolean isStep1(EMDV4Component component){
        return component.getStage().equals(1);
    }

    private Boolean isStep1(EMDV4StudentTaskBook block){
        return block.getStage().equals(1);
    }
}
