package com.iecube.community.model.remote_project.dto;

import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import lombok.Data;

import java.util.List;

@Data
public class DeviceAppointmentsDto {
    Integer deviceId;
    String deviceName;
    List<RemoteAppointment> appointmentList;
}
