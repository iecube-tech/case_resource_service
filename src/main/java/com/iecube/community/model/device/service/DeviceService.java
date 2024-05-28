package com.iecube.community.model.device.service;

import com.iecube.community.model.device.dto.VncResponse;
import com.iecube.community.model.device.entity.Device;
import com.iecube.community.model.device.entity.DeviceType;
import com.iecube.community.model.device.qo.DeviceQo;
import com.iecube.community.model.device.qo.RemoteDeviceQo;
import com.iecube.community.model.device.vo.DeviceVo;

import java.util.List;

public interface DeviceService {
    void addDevice(DeviceQo deviceQo, Integer user);

    List<DeviceVo> allDevice(Integer user);

    void addRemoteDevice(RemoteDeviceQo remoteDeviceQo, Integer user);

    List<DeviceType> allDeviceType();

    void delRemoteDevice(Integer deviceId);

    Device changeRemoteControl(Integer id, Integer targetState, Integer user);

    String connectOnlineBoxTest(String ip, Integer port);

    VncResponse startVnc(String ip, Integer vnc, Integer listen);
}
