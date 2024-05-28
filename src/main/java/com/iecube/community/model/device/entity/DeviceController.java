package com.iecube.community.model.device.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class DeviceController extends BaseEntity {
    Integer id;
    String snId;
    String name;
    Integer collage;
    String version;
    Integer status;
}
