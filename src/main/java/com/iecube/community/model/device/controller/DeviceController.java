package com.iecube.community.model.device.controller;

import com.iecube.community.basecontroller.device.DeviceBaseController;
import com.iecube.community.model.device.dto.VncResponse;
import com.iecube.community.model.device.entity.Device;
import com.iecube.community.model.device.entity.DeviceType;
import com.iecube.community.model.device.qo.DeviceQo;
import com.iecube.community.model.device.qo.RemoteDeviceQo;
import com.iecube.community.model.device.service.DeviceService;
import com.iecube.community.model.device.vo.DeviceVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/device")
public class DeviceController extends DeviceBaseController {
    @Autowired
    private DeviceService deviceService;

    @PostMapping("/add_device")
    public JsonResult<Void> addDevice(@RequestBody DeviceQo deviceQo, HttpSession session){
        Integer user = getUserIdFromSession(session);
        deviceService.addDevice(deviceQo, user);
        return new JsonResult<>(OK);
    }

    @GetMapping("/all")
    public JsonResult<List> allDevice(HttpSession session){
        Integer user = getUserIdFromSession(session);
        List<DeviceVo> deviceVoList = deviceService.allDevice(user);
        return new JsonResult<>(OK, deviceVoList);
    }

    @GetMapping("/remote_type")
    public JsonResult<List> remoteType(){
        List<DeviceType> deviceTypeList = deviceService.allDeviceType();
        return new JsonResult<>(OK, deviceTypeList);
    }

    @PostMapping("/add_remote_device/{pId}")
    public JsonResult<List> addRemoteDevice(@RequestBody RemoteDeviceQo remoteDeviceQo, @PathVariable Integer pId, HttpSession session){
        Integer user = getUserIdFromSession(session);
        remoteDeviceQo.setPId(pId);
        deviceService.addRemoteDevice(remoteDeviceQo, user);
        List<DeviceVo> deviceVoList = deviceService.allDevice(user);
        return new JsonResult<>(OK,deviceVoList);
    }

    @PostMapping("/del_remote_device")
    public JsonResult<List> delRemoteDevice(Integer deviceId, HttpSession session){
        Integer user = getUserIdFromSession(session);
        deviceService.delRemoteDevice(deviceId);
        List<DeviceVo> deviceVoList = deviceService.allDevice(user);
        return new JsonResult<>(OK, deviceVoList);
    }

    @PostMapping("/change_remote_control")
    public JsonResult<Device> changeRemoteControl(Integer id, Integer targetState, HttpSession session){
        Integer user = getUserIdFromSession(session);
        Device device = deviceService.changeRemoteControl(id, targetState,user);
        return new JsonResult<>(OK, device);
    }

    @PostMapping("/refresh_status")
    public JsonResult<Device> refreshStatus(Integer id, HttpSession session){
        Integer user = getUserIdFromSession(session);
        Device device = deviceService.refreshDeviceStatus(id, user);
        return new JsonResult<>(OK, device);
    }

    // test util
    @GetMapping("/connect")
    public JsonResult<String> connectOnlineBox(String ip, Integer port){
        String res = deviceService.connectOnlineBoxTest(ip, port);
        System.out.println("res:"+res);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/start_vnc")
    public JsonResult<VncResponse> startVnc(String ip, Integer vnc, Integer listen){
        VncResponse vncResponse = deviceService.startVnc(ip, vnc, listen);
        System.out.println(vncResponse);
        return new JsonResult<>(OK,vncResponse);
    }
}
