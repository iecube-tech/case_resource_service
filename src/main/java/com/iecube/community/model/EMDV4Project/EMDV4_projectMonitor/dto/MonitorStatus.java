package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto;

import lombok.Getter;

@Getter
public enum MonitorStatus{
    NOTSTART("未开始", 0),
    DOING("进行中", 1),
    DONE("已完成", 2);

    private final String desc;
    private final int status;

    MonitorStatus(String desc, int status) {
        this.desc = desc;
        this.status = status;
    }
}