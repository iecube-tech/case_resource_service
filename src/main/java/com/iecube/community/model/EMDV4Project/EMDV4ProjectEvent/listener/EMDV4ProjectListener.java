package com.iecube.community.model.EMDV4Project.EMDV4ProjectEvent.listener;

import com.iecube.community.model.EMDV4Project.EMDV4ProjectEvent.event.ComputeProjectScore;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.service.EMDV4ProjectStudentService;
import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EMDV4ProjectListener {

    private final EMDV4ProjectStudentService emdV4ProjectStudentService;

    @Autowired
    public EMDV4ProjectListener(EMDV4ProjectStudentService eMDV4ProjectStudentService) {
        this.emdV4ProjectStudentService = eMDV4ProjectStudentService;
    }

    @Async
    @EventListener
    public void handleComputeProjectScore(ComputeProjectScore event) {
        EMDV4ProjectStudentTask changed=event.getChanged();
        emdV4ProjectStudentService.computeScore(changed);
    }
}
