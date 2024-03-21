package com.iecube.community.model.iecube3835.mapper;

import com.iecube.community.model.iecube3835.entity.PSTDetailDevice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PSTDetailsDeviceMapper {
    Integer insert(PSTDetailDevice pstDetailDevice);

    Integer updateData(PSTDetailDevice pstDetailDevice);

    PSTDetailDevice getByPSTId(Integer pstId);

    PSTDetailDevice getById(Integer pstId);

    Integer updateSubmit(Boolean submit);
}
