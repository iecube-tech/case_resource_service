package com.iecube.community.model.pst_devicelog.mapper;

import com.iecube.community.model.pst_devicelog.dto.PSTDeviceLogParseDto;
import com.iecube.community.model.pst_devicelog.dto.StudentLogOverview;
import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PSTDeviceLogMapper {
    Integer addDeviceLog(PSTDeviceLog pstDeviceLog);

    List<PSTDeviceLog> getPSTDeviceLogs(Integer pstId);

    TaskInfo getTaskInfoByPSTId(Integer pstId);


    //logParse
    Integer insertPSTLogParse(PSTDeviceLogParseDto pstDeviceLogParseDto);

    PSTDeviceLogParseDto getPSTLogParseByPstId(Integer pstId);

    Integer updatePSTLogParse(PSTDeviceLogParseDto pstDeviceLogParseDto);

    List<StudentLogOverview> getProjectLogCompare(Integer projectId);

}
