package com.iecube.community.model.project.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RemoteQo {
    private String  startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Integer appointmentDuration;
    private Integer appointmentCount;
    private Integer dayLimit;
    private List<Integer> remoteDeviceIdList;
}
