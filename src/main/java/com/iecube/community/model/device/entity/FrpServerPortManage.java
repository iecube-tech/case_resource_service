package com.iecube.community.model.device.entity;

import lombok.Data;

@Data
public class FrpServerPortManage {
    Integer id;
    Integer deviceId;
    Integer frpServerId;
    Integer remotePort;
    Integer vncPort;
    Integer vncPid;
}
