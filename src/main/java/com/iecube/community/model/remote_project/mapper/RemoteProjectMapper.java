package com.iecube.community.model.remote_project.mapper;

import com.iecube.community.model.remote_project.entity.RemoteProject;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RemoteProjectMapper {
    Integer addRemoteProject(RemoteProject remoteProject);

    List<RemoteProject> checkDeviceTime(Integer deviceId, LocalDate startDate, LocalDate endDate);

    RemoteProject getByProjectId(Integer projectId);
}
