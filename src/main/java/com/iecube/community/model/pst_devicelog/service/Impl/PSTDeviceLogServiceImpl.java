package com.iecube.community.model.pst_devicelog.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import com.iecube.community.model.pst_devicelog.mapper.PSTDeviceLogMapper;
import com.iecube.community.model.pst_devicelog.service.PSTDeviceLogService;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.mapper.ResourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PSTDeviceLogServiceImpl implements PSTDeviceLogService {
    @Autowired
    private PSTDeviceLogMapper pstDeviceLogMapper;

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public PSTDeviceLog uploadPSTDeviceLog(Integer pstId, Integer resourceId) {
        PSTDeviceLog pstDeviceLog = new PSTDeviceLog();
        pstDeviceLog.setPstId(pstId);
        pstDeviceLog.setResourceId(resourceId);
        Integer row = pstDeviceLogMapper.addDeviceLog(pstDeviceLog);
        if(row != 1){
            throw new InsertException("添加数据异常");
        }
    return pstDeviceLog;
    }

    @Override
    public List<Resource> getPSTDeviceLogsByPstId(Integer pstId) {
        List<PSTDeviceLog> pstDeviceLogList = pstDeviceLogMapper.getPSTDeviceLogs(pstId);
        List<Resource> resourceList = new ArrayList<>();
        for(PSTDeviceLog pstDeviceLog : pstDeviceLogList){
            System.out.println(pstDeviceLog);
            Resource resource = resourceMapper.getById(pstDeviceLog.getResourceId());
            resourceList.add(resource);
        }
        return resourceList;
    }

    @Override
    public TaskInfo getTaskDetailByPSTId(Integer pstId) {
        TaskInfo taskInfo=pstDeviceLogMapper.getTaskInfoByPSTId(pstId);
        return taskInfo;
    }
}
