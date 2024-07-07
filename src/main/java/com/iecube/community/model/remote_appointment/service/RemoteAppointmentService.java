package com.iecube.community.model.remote_appointment.service;

import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.qo.RemoteAppointmentQo;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;

import java.util.List;

public interface RemoteAppointmentService {

    List<RemoteAppointmentVo> studentAppointmentList(Integer projectId, Integer studentId);

    List<RemoteAppointment> getAppointmentList(RemoteAppointmentQo appointmentQo);

    void studentAppointment(Integer appointmentId, Integer studentId);

    void studentCancelAppointment(Integer appointmentId, Integer studentId);
}
