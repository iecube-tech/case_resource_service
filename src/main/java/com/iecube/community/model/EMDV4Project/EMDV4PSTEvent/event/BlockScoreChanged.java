package com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event;

import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BlockScoreChanged extends ApplicationEvent {
    private final EMDV4StudentTaskBook changedBlock;
    public BlockScoreChanged(Object source, EMDV4StudentTaskBook changed) {
        super(source);
        this.changedBlock = changed;
    }
}

