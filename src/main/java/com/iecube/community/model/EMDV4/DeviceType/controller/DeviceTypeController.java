package com.iecube.community.model.EMDV4.DeviceType.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.DeviceType.entity.DeviceType;
import com.iecube.community.model.EMDV4.DeviceType.service.DeviceTypeService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emdv4/device_type/")
public class DeviceTypeController extends BaseController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @GetMapping("/all_device_type")
    public JsonResult<List<DeviceType>> getAllDeviceTypes() {
        return new JsonResult<>(OK, deviceTypeService.getAll());
    }
}
