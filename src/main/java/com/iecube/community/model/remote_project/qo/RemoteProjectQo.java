package com.iecube.community.model.remote_project.qo;

import com.iecube.community.model.remote_project.entity.RemoteProject;
import com.iecube.community.model.remote_project_join_device.entity.RemoteProjectDevice;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RemoteProjectQo {
    RemoteProject remoteProject;
    List<RemoteProjectDevice> remoteProjectDeviceList;
}
