package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.dto.TaskStepDto;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectMonitorMapper {
    MonitorInfoVo getMonitorInfo(Integer projectId);

    List<TaskStepDto> getAllStuTaskStep(Integer projectId);
}
