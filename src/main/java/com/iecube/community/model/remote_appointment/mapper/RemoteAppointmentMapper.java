package com.iecube.community.model.remote_appointment.mapper;

import com.iecube.community.model.remote_appointment.entity.RemoteAppointment;
import com.iecube.community.model.remote_appointment.vo.RemoteAppointmentVo;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RemoteAppointmentMapper {
    Integer batchAdd(List<RemoteAppointment> remoteAppointmentList);

    List<LocalDate> listAppointmentDate(Integer projectId); // 可预约的日期列表

    List<RemoteAppointment> listByDeviceAndDate(Integer projectId, Integer deviceId, LocalDate appointmentDate); // 该项目当天的设备可预约的时间列表

    List<RemoteAppointmentVo> studentAppointmentList(Integer projectId, Integer studentId); // 学生已预约的列表

    RemoteAppointment getById(Integer appointmentId);

    Integer studentAppointment(Integer appointmentId, Integer studentId);

    Integer studentCancelAppointment(Integer appointmentId, Integer studentId);
}
