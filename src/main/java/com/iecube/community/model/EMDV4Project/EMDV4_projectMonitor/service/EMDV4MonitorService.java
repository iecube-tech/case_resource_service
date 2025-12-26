package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;

public interface EMDV4MonitorService {

    MonitorInfoVo getMonitorInfo(Integer projectId);
}
