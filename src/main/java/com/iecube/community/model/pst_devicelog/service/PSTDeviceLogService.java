package com.iecube.community.model.pst_devicelog.service;

import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import com.iecube.community.model.resource.entity.Resource;

import java.util.List;

public interface PSTDeviceLogService {
    PSTDeviceLog uploadPSTDeviceLog(Integer pstId, Integer resourceId);

    List<Resource> getPSTDeviceLogsByPstId(Integer pstId);

    TaskInfo getTaskDetailByPSTId(Integer pstId);
}
