package com.iecube.community.model.remote_project_join_device.mapper;

import com.iecube.community.model.remote_project_join_device.dto.RemoteDeviceDto;
import com.iecube.community.model.remote_project_join_device.entity.RemoteProjectDevice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RemoteProjectDeviceMapper {
    Integer batchAddRemoteProjectDevice(List<RemoteProjectDevice> remoteProjectDeviceList);

    List<RemoteDeviceDto> listByProjectId(Integer projectId);
}
