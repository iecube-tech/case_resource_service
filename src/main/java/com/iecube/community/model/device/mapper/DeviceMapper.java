package com.iecube.community.model.device.mapper;

import com.iecube.community.model.device.entity.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface DeviceMapper {
//    device
    Integer addDevice(Device device);
    Integer delDevice(Integer id);
    Integer upDevice(Device device);
    Device getDeviceById(Integer id);
    List<Device> getDeviceByPid(Integer pId);
    List<Device> getDeviceByCollage(Integer collage);

    Device getDeviceByPIdAndIp(Integer pId, String ip);

    Integer updateRemoteControl(Integer id, Integer remoteControl,Integer deviceState, Integer pid, Integer lastModifiedUser, Date lastModifiedTime);

    Integer updateDeviceState(Integer id, Integer deviceState, Integer pid);

    Integer updateRemoteUrl(Integer id, String remoteUrl);

//    DeviceController
    Integer addDeviceController(DeviceController deviceController);
    Integer delDeviceController(Integer id);
    Integer upDeviceController(DeviceController deviceController);
    DeviceController getDeviceControllerById(Integer id);
    List<DeviceController> getDeviceControllerByCollage(Integer collage);


//    DeviceType
    Integer addDeviceType(DeviceType deviceType);
    Integer delDeviceType(Integer id);
    Integer upDeviceType(DeviceType deviceType);
    List<DeviceType> getAllDeviceType();

//    FrpServer
    FrpServer getFrpServerById(Integer id);

//    FrpServerPortManage
    Integer addFrpServerPortManage(FrpServerPortManage frpServerPortManage);
    FrpServerPortManage getFrpServerPortManageByDeviceId(Integer deviceId);

    Integer deleteFrpPortManageByDeviceId(Integer deviceId);

    Integer updateVncPid(FrpServerPortManage frpServerPortManage);

}
