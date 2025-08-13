package com.iecube.community.model.EMDV4.DeviceType.entity;

import com.iecube.community.model.EMDV4.TopMajorField.entity.MajorField;
import lombok.Data;

@Data
public class DeviceType {
    private Long id;
    private Long MF;
    private String name;
    private MajorField major;
}
