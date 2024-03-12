package com.iecube.community.model.iecube_device.controller;

import com.iecube.community.basecontroller.iecube_device.IecubeDeviceBaseController;
import com.iecube.community.model.iecube_device.entity.IecubeDevice;
import com.iecube.community.model.iecube_device.service.IecubeDeviceService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iecube_device")
public class IecubeDeviceController extends IecubeDeviceBaseController {

    @Autowired
    private IecubeDeviceService iecubeDeviceService;

    @GetMapping("/all")
    public JsonResult<List> allDevices(){
        List<IecubeDevice> devices = iecubeDeviceService.allDevice();
        return new JsonResult<>(OK, devices);
    }

    @PostMapping("/add")
    public  JsonResult<Void> addDevice(@RequestBody IecubeDevice iecubeDevice){
        iecubeDeviceService.addDevice(iecubeDevice);
        return new JsonResult<>(OK);
    }
}
