package com.iecube.community.model.device.qo;

import lombok.Data;

@Data
public class RemoteDeviceQo {
    String snId;
    String name;
    Integer pId; // pid != 0  才可以拥有以下字段
    Integer type;
    String ip;
    Integer port;
    String liveUrl;
}
