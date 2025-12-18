package com.iecube.community.model.remote_project.service.impl;

import com.iecube.community.model.auth.service.ex.AuthCodeErrorException;
import com.iecube.community.model.device.entity.Device;
import com.iecube.community.model.device.mapper.DeviceMapper;
import com.iecube.community.model.project.mapper.ProjectMapper;
import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.mapper.RemoteAppointmentMapper;
import com.iecube.community.model.remote_appointment.qo.RemoteAppointmentQo;
import com.iecube.community.model.remote_appointment.service.ex.AppointmentFailedException;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;
import com.iecube.community.model.remote_project.dto.DeviceAppointmentsDto;
import com.iecube.community.model.remote_project.entity.RemoteProject;
import com.iecube.community.model.remote_project.mapper.RemoteProjectMapper;
import com.iecube.community.model.remote_project.qo.RemoteProjectQo;
import com.iecube.community.model.remote_project.service.RemoteProjectService;
import com.iecube.community.model.remote_project.service.ex.DeviceHasBeenUsedException;
import com.iecube.community.model.remote_project.vo.RemoteOperationVo;
import com.iecube.community.model.remote_project.vo.RemoteProjectVo;
import com.iecube.community.model.remote_project_join_device.dto.RemoteDeviceDto;
import com.iecube.community.model.remote_project_join_device.entity.RemoteProjectDevice;
import com.iecube.community.model.remote_project_join_device.mapper.RemoteProjectDeviceMapper;
import com.iecube.community.ys.YsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RemoteProjectServiceImpl implements RemoteProjectService {
    @Autowired
    private RemoteProjectMapper remoteProjectMapper;

    @Autowired
    private RemoteProjectDeviceMapper remoteProjectDeviceMapper;

    @Autowired
    private RemoteAppointmentMapper remoteAppointmentMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private YsApi ysApi;

    @Value("${ys.enable}")
    private Boolean ysEnable;

    @Override
    public void addRemoteProject(RemoteProjectQo remoteProjectQo) {
        RemoteProject remoteProject = remoteProjectQo.getRemoteProject();
        // 设备冲突检查
        List<RemoteProjectDevice> remoteProjectDeviceList = remoteProjectQo.getRemoteProjectDeviceList();
        remoteProjectDeviceList.forEach(remoteProjectDevice -> {
            checkDeviceTime(remoteProject,remoteProjectDevice.getDeviceId());
        });
        Integer res = remoteProjectMapper.addRemoteProject(remoteProject);
        Integer res1 = remoteProjectDeviceMapper.batchAddRemoteProjectDevice(remoteProjectDeviceList);
        // 对每一个device按照单次时长拆分总时长 生成RemoteAppointment
        batchAddAppointment(remoteProject, remoteProjectDeviceList);
    }

    private void checkDeviceTime(RemoteProject newRemoteProject, Integer deviceId){
        List<RemoteProject> remoteProjects = remoteProjectMapper.checkDeviceTime(deviceId, newRemoteProject.getStartDate(), newRemoteProject.getEndDate());
        if(remoteProjects.size()>0){
            String Msg = "";
            for (RemoteProject remoteProject : remoteProjects) {
                Device device = deviceMapper.getDeviceById(deviceId);
                String message = "设备：" + device.getName() + " 在" +
                        remoteProject.getStartDate()+ "至" + remoteProject.getEndDate() + " " + "已被其他项目使用,请重新选择时间。";
                Msg = Msg + message;
            }
            projectMapper.delete(newRemoteProject.getProjectId());
            throw new DeviceHasBeenUsedException(Msg);
        }
    }

    private void batchAddAppointment(RemoteProject dto, List<RemoteProjectDevice> remoteProjectDeviceList) {
        LocalTime startTime = dto.getStartTime();
        LocalTime endTime = dto.getEndTime();
        Duration timeDuration = Duration.between(startTime, endTime);
        int timeCount = (int) timeDuration.toMinutes();
        LocalDate startDate = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        long days = end.toEpochDay() - startDate.toEpochDay();
        LocalTime temp = startTime;
        List<RemoteAppointment> appointmentList = new ArrayList<>();
        for (RemoteProjectDevice device : remoteProjectDeviceList) {
            for (long i = days; i >= 0; i--) {
                while ((timeCount = timeCount - dto.getAppointmentDuration()) > -1) {
                    RemoteAppointment remoteAppointment = new RemoteAppointment();
                    remoteAppointment.setProjectId(dto.getProjectId());
                    remoteAppointment.setStatus(1);
                    remoteAppointment.setDeviceId(device.getDeviceId());
                    LocalDate now = startDate.plusDays(i);
                    remoteAppointment.setAppointmentDate(now);
                    remoteAppointment.setAppointmentStartTime(temp);
                    remoteAppointment.setAppointmentEndTime(temp.plusMinutes(dto.getAppointmentDuration()));
                    temp = temp.plusMinutes(dto.getAppointmentDuration());
                    appointmentList.add(remoteAppointment);
                }
                temp = startTime;
                timeCount = (int) timeDuration.toMinutes();
            }
        }
        Integer res = remoteAppointmentMapper.batchAdd(appointmentList);
    }

    @Override
    public RemoteProjectVo studentGetRemoteProject(Integer projectId){
        RemoteProject remoteProject = remoteProjectMapper.getByProjectId(projectId);
        if(remoteProject==null){
            return null;
        }
        List<RemoteDeviceDto> deviceList = remoteProjectDeviceMapper.listByProjectId(projectId);
        List<LocalDate> dateList = remoteAppointmentMapper.listAppointmentDate(projectId);
        RemoteProjectVo remoteProjectVo = new RemoteProjectVo();
        remoteProjectVo.setId(remoteProject.getId());
        remoteProjectVo.setProjectId(remoteProject.getProjectId());
        remoteProjectVo.setStartDate(remoteProject.getStartDate());
        remoteProjectVo.setEndDate(remoteProject.getEndDate());
        remoteProjectVo.setStartTime(remoteProject.getStartTime());
        remoteProjectVo.setEndTime(remoteProject.getEndTime());
        remoteProjectVo.setAppointmentDuration(remoteProject.getAppointmentDuration());
        remoteProjectVo.setDayLimit(remoteProject.getDayLimit());
        remoteProjectVo.setAppointmentCount(remoteProject.getAppointmentCount());
        remoteProjectVo.setRemoteDeviceList(deviceList);
        remoteProjectVo.setAppointmentDateList(dateList);
        return remoteProjectVo;
    }

    @Override
    public RemoteOperationVo remoteOperation(Integer appointmentId, Integer studentId){
        RemoteAppointment remoteAppointment = remoteAppointmentMapper.getById(appointmentId);
        if(remoteAppointment.getStudentId() == null){
            throw new AuthCodeErrorException("非本人操作");
        }
        if(!remoteAppointment.getStudentId().equals(studentId)){
            throw new AuthCodeErrorException("非本人操作");
        }
        LocalTime startTime = remoteAppointment.getAppointmentStartTime();
        LocalTime stopTime = remoteAppointment.getAppointmentEndTime();
        LocalDate appointmentDate = remoteAppointment.getAppointmentDate();
        LocalDateTime startDateTime = LocalDateTime.of(appointmentDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(appointmentDate,stopTime);
        LocalDateTime now = LocalDateTime.now();
        if(now.isBefore(startDateTime)){
            throw new AppointmentFailedException("预约的远程操作尚未开始");
        }
        if(now.isAfter(endDateTime)){
            throw new AppointmentFailedException("预约的远程操作已经结束");
        }
        Device device = deviceMapper.getDeviceById(remoteAppointment.getDeviceId());
        RemoteOperationVo remoteOperationVo = new RemoteOperationVo();
        remoteOperationVo.setId(remoteAppointment.getId());
        remoteOperationVo.setStudentId(remoteAppointment.getStudentId());
        remoteOperationVo.setProjectId(remoteAppointment.getProjectId());
        remoteOperationVo.setDeviceId(device.getId());
        remoteOperationVo.setDeviceName(device.getName());
        remoteOperationVo.setDeviceState(device.getDeviceState());
        remoteOperationVo.setRemoteControl(device.getRemoteControl());
        remoteOperationVo.setRemoteUrl(device.getRemoteUrl());
        remoteOperationVo.setLiveUrl(device.getLiveUrl());
        remoteOperationVo.setAppointmentDate(remoteAppointment.getAppointmentDate());
        remoteOperationVo.setAppointmentStartTime(remoteAppointment.getAppointmentStartTime());
        remoteOperationVo.setAppointmentEndTime(remoteAppointment.getAppointmentEndTime());
        if(ysEnable){
            remoteOperationVo.setYsAccessToken(ysApi.getAccessToken());
        }
        return remoteOperationVo;
    }

    @Override
    public void deleteRemoteProject(Integer projectId) {
        remoteProjectMapper.deleteRemoteProject(projectId);
    }
}
