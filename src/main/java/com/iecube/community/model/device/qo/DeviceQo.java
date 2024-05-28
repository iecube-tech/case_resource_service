package com.iecube.community.model.device.qo;

import lombok.Data;

@Data
public class DeviceQo {
    Integer type; // 0 远程控制器  1 普通设备
    Integer id;
    String snId;
    String name;
}
