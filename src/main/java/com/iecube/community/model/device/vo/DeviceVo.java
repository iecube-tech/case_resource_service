package com.iecube.community.model.device.vo;

import com.iecube.community.model.device.entity.Device;
import lombok.Data;

import java.util.List;

@Data
public class DeviceVo {
    Integer id;
    Integer type;   // 0 远程控制器  1 普通设备
    String snId;
    String name;
    String version;
    Integer status;
    List<Device> remoteDevices;
}
