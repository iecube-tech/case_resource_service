package com.iecube.community.model.iecube_device.mapper;

import com.iecube.community.model.iecube_device.entity.IecubeDevice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IecubeDeviceMapper {
    Integer add(IecubeDevice iecubeDevice);
    List<IecubeDevice> all();
}
