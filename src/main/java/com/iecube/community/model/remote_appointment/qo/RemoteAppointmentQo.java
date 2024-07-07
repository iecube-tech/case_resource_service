package com.iecube.community.model.remote_appointment.qo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RemoteAppointmentQo {
    Integer projectId;
    Integer deviceId;
    LocalDate appointmentDate;
}
