package com.iecube.community.model.remote_project.service;


import com.iecube.community.model.remote_project.entity.RemoteProject;
import com.iecube.community.model.remote_project.qo.RemoteProjectQo;
import com.iecube.community.model.remote_project_join_device.entity.RemoteProjectDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class RemoteProjectServiceTests {

    @Autowired
    private RemoteProjectService remoteProjectService;

    @Test
    public void addRemoteProjectTest(){
        RemoteProject remoteProject = new RemoteProject();
        remoteProject.setProjectId(34);
        remoteProject.setStartDate(LocalDate.of(2024,6,28));
        remoteProject.setStartTime(LocalTime.of(0,0));
        remoteProject.setEndDate(LocalDate.of(2024,6,29));
        remoteProject.setEndTime(LocalTime.of(13,0));
        remoteProject.setAppointmentCount(3);
        remoteProject.setDayLimit(2);
        remoteProject.setAppointmentDuration(60);

        RemoteProjectDevice remoteProjectDevice = new RemoteProjectDevice();
        List<RemoteProjectDevice> remoteProjectDeviceList = new ArrayList<>();
        remoteProjectDevice.setProjectId(34);
        remoteProjectDevice.setDeviceId(3);
        remoteProjectDeviceList.add(remoteProjectDevice);
        RemoteProjectQo remoteProjectQo = new RemoteProjectQo();
        remoteProjectQo.setRemoteProject(remoteProject);
        remoteProjectQo.setRemoteProjectDeviceList(remoteProjectDeviceList);
        remoteProjectService.addRemoteProject(remoteProjectQo);
    }
}
