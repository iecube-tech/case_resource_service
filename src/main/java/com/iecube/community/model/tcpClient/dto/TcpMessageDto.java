package com.iecube.community.model.tcpClient.dto;

import lombok.Data;

@Data
public class TcpMessageDto {
    String type;
    DeviceDetail deviceDetail;
}
