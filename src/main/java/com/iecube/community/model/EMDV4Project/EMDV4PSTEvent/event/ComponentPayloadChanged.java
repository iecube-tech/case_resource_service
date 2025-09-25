package com.iecube.community.model.EMDV4Project.EMDV4PSTEvent.event;

import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ComponentPayloadChanged extends ApplicationEvent {
    private final EMDV4Component component;
    public ComponentPayloadChanged(Object source, EMDV4Component changed) {
        super(source);
        this.component = changed;
    }
}
