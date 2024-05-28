package com.iecube.community.model.device.entity;

import com.iecube.community.entity.BaseEntity;
import lombok.Data;

@Data
public class Device extends BaseEntity {
    Integer id;
    String snId;
    String name;
    Integer collageId;
    Integer pId; // pid != 0  才可以拥有以下字段
    Integer type;
    String typeName;
    String ip;
    Integer port;
    String liveUrl;
    Integer deviceState; // 在线 离线  ping ip地址是否能ping通， 对应的端口是否开放
    Integer remoteControl;   // 开启关闭远程 0 off  1 on --展示当前的状态， 由控制器返回
    String remoteUrl;
    Integer boxPid;
    Integer singleDuration;  // 设备单次时长
    Integer allowAppointment; // 是否允许预约
}
