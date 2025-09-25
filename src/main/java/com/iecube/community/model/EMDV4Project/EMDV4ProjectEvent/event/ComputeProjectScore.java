package com.iecube.community.model.EMDV4Project.EMDV4ProjectEvent.event;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ComputeProjectScore extends ApplicationEvent {
    private final EMDV4ProjectStudentTask changed;

    public ComputeProjectScore(Object source, EMDV4ProjectStudentTask changed) {
        super(source);
        this.changed = changed;
    }
}
