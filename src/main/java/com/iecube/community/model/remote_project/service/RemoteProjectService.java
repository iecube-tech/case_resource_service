package com.iecube.community.model.remote_project.service;

import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.qo.RemoteAppointmentQo;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;
import com.iecube.community.model.remote_project.qo.RemoteProjectQo;
import com.iecube.community.model.remote_project.vo.RemoteOperationVo;
import com.iecube.community.model.remote_project.vo.RemoteProjectVo;

import java.util.List;

public interface RemoteProjectService {
    void addRemoteProject(RemoteProjectQo remoteProjectQo);

    RemoteProjectVo studentGetRemoteProject(Integer projectId);

    RemoteOperationVo remoteOperation(Integer appointmentId, Integer studentId);
}
