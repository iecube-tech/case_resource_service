package com.iecube.community.model.tcpClient.dto;

import lombok.Data;

@Data
public class DeviceDetail {
    Integer id;
    Integer pid;
    String frpServerIp;
    Integer frpServerPort;
    String localIp;
    Integer localPort;
    Integer remotePort;
}
