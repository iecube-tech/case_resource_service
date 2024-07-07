package com.iecube.community.model.remote_project.vo;

import com.iecube.community.model.remote_project.dto.DeviceAppointmentsDto;
import com.iecube.community.model.remote_project_join_device.dto.RemoteDeviceDto;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RemoteProjectVo {
    Integer id;
    Integer projectId;
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    LocalTime endTime;
    Integer appointmentDuration;
    Integer appointmentCount;
    Integer dayLimit;
    List<RemoteDeviceDto> remoteDeviceList;
    List<LocalDate> appointmentDateList;
}
