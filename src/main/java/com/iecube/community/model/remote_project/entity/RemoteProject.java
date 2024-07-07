package com.iecube.community.model.remote_project.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
public class RemoteProject {
    Integer id;
    Integer projectId;
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    LocalTime endTime;
    Integer appointmentDuration;
    Integer appointmentCount;
    Integer dayLimit;
}
