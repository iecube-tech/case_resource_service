package com.iecube.community.model.iecube_device.service;

import com.iecube.community.model.iecube_device.entity.IecubeDevice;

import java.util.List;

public interface IecubeDeviceService {
    List<IecubeDevice> allDevice();

    void addDevice(IecubeDevice iecubeDevice);
}
