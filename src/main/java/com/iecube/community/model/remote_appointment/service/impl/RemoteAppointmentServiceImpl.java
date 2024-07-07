package com.iecube.community.model.remote_appointment.service.impl;

import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.mapper.RemoteAppointmentMapper;
import com.iecube.community.model.remote_appointment.qo.RemoteAppointmentQo;
import com.iecube.community.model.remote_appointment.service.RemoteAppointmentService;
import com.iecube.community.model.remote_appointment.service.ex.AppointmentFailedException;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;
import com.iecube.community.model.remote_project.entity.RemoteProject;
import com.iecube.community.model.remote_project.mapper.RemoteProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemoteAppointmentServiceImpl implements RemoteAppointmentService {

    @Autowired
    private RemoteProjectMapper remoteProjectMapper;

    @Autowired
    private RemoteAppointmentMapper remoteAppointmentMapper;

    @Override
    public List<RemoteAppointment> getAppointmentList(RemoteAppointmentQo appointmentQo){
        if(appointmentQo.getProjectId() == null||appointmentQo.getDeviceId() == null|| appointmentQo.getAppointmentDate() == null){
            return new ArrayList<>();
        }
        List<RemoteAppointment> remoteAppointmentList = remoteAppointmentMapper.listByDeviceAndDate(
                appointmentQo.getProjectId(), appointmentQo.getDeviceId(), appointmentQo.getAppointmentDate());
        return remoteAppointmentList;
    }

    @Override
    public List<RemoteAppointmentVo> studentAppointmentList(Integer projectId, Integer studentId){
        List<RemoteAppointmentVo> remoteAppointmentVoList = remoteAppointmentMapper.studentAppointmentList(projectId, studentId);
        return remoteAppointmentVoList;
    }

    @Override
    public void studentAppointment(Integer appointmentId, Integer studentId){
        RemoteAppointment remoteAppointment = remoteAppointmentMapper.getById(appointmentId);
        if(!remoteAppointment.getStatus().equals(1)){
            throw new AppointmentFailedException("该时间段已被预约，请重新选择");
        }
        RemoteProject remoteProject = remoteProjectMapper.getByProjectId(remoteAppointment.getProjectId());
        List<RemoteAppointmentVo> remoteAppointmentVoList = remoteAppointmentMapper.studentAppointmentList(remoteProject.getProjectId(), studentId);
        Integer curAppointmentCount = remoteAppointmentVoList.size();
        if(curAppointmentCount >= remoteProject.getAppointmentCount()){
            throw new AppointmentFailedException("预约次数已达上限！");
        }
        Integer res = remoteAppointmentMapper.studentAppointment(appointmentId, studentId);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void studentCancelAppointment(Integer appointmentId, Integer studentId){
        RemoteAppointment remoteAppointment = remoteAppointmentMapper.getById(appointmentId);
        LocalTime startTime = remoteAppointment.getAppointmentStartTime();
        LocalDate startDate = remoteAppointment.getAppointmentDate();
        LocalDateTime startDateTime  = LocalDateTime.of(startDate, startTime);
        LocalDateTime now = LocalDateTime.now();
        if(now.isAfter(startDateTime)){
            throw new AppointmentFailedException("已过预约时间，取消失败");
        }
        Integer res = remoteAppointmentMapper.studentCancelAppointment(appointmentId, studentId);
        if(res != 1){
            throw new UpdateException("更新数据异常");
        }
    }
}
