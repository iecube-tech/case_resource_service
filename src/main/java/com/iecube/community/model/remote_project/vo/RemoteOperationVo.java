package com.iecube.community.model.remote_project.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RemoteOperationVo {
    Integer id;
    Integer studentId;
    Integer projectId;
    Integer deviceId;
    String deviceName;
    Integer deviceState; // 在线 离线  ping ip地址是否能ping通， 对应的端口是否开放
    Integer remoteControl;   // 开启关闭远程 0 off  1 on --展示当前的状态， 由控制器返回
    String remoteUrl;
    String liveUrl;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate appointmentDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime appointmentStartTime;
    @JsonFormat(pattern = "HH:mm")
    LocalTime appointmentEndTime;

}
