package com.iecube.community.model.EMDV4.DeviceType.service.impl;

import com.iecube.community.model.EMDV4.DeviceType.entity.DeviceType;
import com.iecube.community.model.EMDV4.DeviceType.mapper.DeviceTypeMapper;
import com.iecube.community.model.EMDV4.DeviceType.service.DeviceTypeService;
import com.iecube.community.model.EMDV4.TopMajorField.entity.MajorField;
import com.iecube.community.model.EMDV4.TopMajorField.mapper.TopMajorFieldMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private TopMajorFieldMapper topMajorFieldMapper;

    @Override
    public List<DeviceType> getAll(){
        List<DeviceType> deviceTypeList = deviceTypeMapper.selectAll();
        for(DeviceType deviceType : deviceTypeList){
            MajorField mf = topMajorFieldMapper.getById(deviceType.getMF());
            deviceType.setMajor(mf);
        }
        return deviceTypeList;
    }
}
