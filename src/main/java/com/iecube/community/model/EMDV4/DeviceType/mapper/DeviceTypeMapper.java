package com.iecube.community.model.EMDV4.DeviceType.mapper;

import com.iecube.community.model.EMDV4.DeviceType.entity.DeviceType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceTypeMapper {
    List<DeviceType> selectAll();
}
