package com.iecube.community.model.iecube3835.service;

import com.iecube.community.model.iecube3835.entity.PSTDetailDevice;
import com.iecube.community.model.task.entity.StudentTaskDetailVo;

public interface DeviceDetailDataService {

    PSTDetailDevice updatePstDetail(PSTDetailDevice pstDetailDevice);

    PSTDetailDevice getByPstId(Integer pstId);

    PSTDetailDevice submit(Integer pstId, Integer studentId);

}
