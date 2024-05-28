package com.iecube.community.model.device.entity;

import lombok.Data;

@Data
public class FrpServer {
    Integer id;
    String name;
    String ip;
    Integer port;
}
