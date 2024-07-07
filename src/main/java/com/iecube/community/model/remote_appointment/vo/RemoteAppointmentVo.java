package com.iecube.community.model.remote_appointment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RemoteAppointmentVo {
    Integer id;
    Integer studentId;
    Integer projectId;
    String projectName;
    Integer deviceId;
    String deviceName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate appointmentDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime appointmentStartTime;
    @JsonFormat(pattern = "HH:mm")
    LocalTime appointmentEndTime;
    Integer status;
}
