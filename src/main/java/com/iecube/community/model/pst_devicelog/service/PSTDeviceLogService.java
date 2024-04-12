package com.iecube.community.model.pst_devicelog.service;

import com.iecube.community.model.pst_devicelog.dto.PSTDeviceLogParseDto;
import com.iecube.community.model.pst_devicelog.dto.StudentLogOverview;
import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import com.iecube.community.model.pst_devicelog.vo.StudentTasksOperations;
import com.iecube.community.model.resource.entity.Resource;

import java.util.List;

public interface PSTDeviceLogService {
    PSTDeviceLog uploadPSTDeviceLog(Integer pstId, Resource resource);

    List<Resource> getPSTDeviceLogsByPstId(Integer pstId);

    TaskInfo getTaskDetailByPSTId(Integer pstId);

    PSTDeviceLogParseDto getLogVisualization(Integer pstId);

    List<StudentTasksOperations> getProjectLogCompare(Integer projectId);
}
