package com.iecube.community.model.iecube_device.entity;

import lombok.Data;

@Data
public class IecubeDevice {
    Integer id;
    String name;
    String connectType;
    String webBasicUrl;
    String basicDataTable;
}
