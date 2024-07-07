package com.iecube.community.model.project.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RemoteQo {
    String  startDate;
    String endDate;
    String startTime;
    String endTime;
    Integer appointmentDuration;
    Integer appointmentCount;
    Integer dayLimit;
    List<Integer> remoteDeviceIdList;
}
