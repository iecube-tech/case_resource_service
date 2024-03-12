package com.iecube.community.model.iecube_device.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.iecube_device.entity.IecubeDevice;
import com.iecube.community.model.iecube_device.mapper.IecubeDeviceMapper;
import com.iecube.community.model.iecube_device.service.IecubeDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IecubeDeviceServiceImpl implements IecubeDeviceService {
    @Autowired
    private IecubeDeviceMapper iecubeDeviceMapper;

    @Override
    public List<IecubeDevice> allDevice() {
        List<IecubeDevice> devices = iecubeDeviceMapper.all();
        return devices;
    }

    @Override
    public void addDevice(IecubeDevice iecubeDevice) {
        Integer row = iecubeDeviceMapper.add(iecubeDevice);
        if(row != 1){
            throw new InsertException("添加数据异常");
        }

    }
}
