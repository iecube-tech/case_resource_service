package com.iecube.community.model.iecube3835.controller;

import com.iecube.community.basecontroller.iecube3835.DeviceDetailDataControllerBaseController;
import com.iecube.community.model.iecube3835.entity.PSTDetailDevice;
import com.iecube.community.model.iecube3835.qo.HtmlValue;
import com.iecube.community.model.iecube3835.service.DeviceDetailDataService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/pst_ddd")
public class DeviceDetailDataController extends DeviceDetailDataControllerBaseController {

    @Autowired
    private DeviceDetailDataService deviceDetailDataService;

    @PostMapping("/up/{pstId}")
    public JsonResult<PSTDetailDevice> updateDeviceDetailData(@PathVariable Integer pstId, @RequestBody PSTDetailDevice pstDetailDevice){
        pstDetailDevice.setPstId(pstId);
        PSTDetailDevice pstDetailDevice1 = deviceDetailDataService.updatePstDetail(pstDetailDevice);
        return new JsonResult<>(OK,pstDetailDevice1);
    }

    @PostMapping("/up/group/{groupId}/{pstId}")
    public JsonResult<PSTDetailDevice> updateGroupDeviceDetailData(@PathVariable Integer groupId, @PathVariable Integer pstId,
                                                                   @RequestBody PSTDetailDevice pstDetailDevice){
        PSTDetailDevice rePSTDetailDevice = deviceDetailDataService.updateGroupPstDetail(groupId,pstId,pstDetailDevice);
        return new JsonResult<>(OK,rePSTDetailDevice);
    }

    @GetMapping("/{pstId}")
    public JsonResult<PSTDetailDevice> getPstDdd(@PathVariable Integer pstId){
        PSTDetailDevice pstDetailDevice = deviceDetailDataService.getByPstId(pstId);
        return new JsonResult<>(OK,pstDetailDevice);
    }

    @GetMapping("/submit")
     public JsonResult<PSTDetailDevice> submit(Integer pstId, HttpSession session){
        Integer studentId =  getUserIdFromSession(session);
        PSTDetailDevice pstDetailDevice = deviceDetailDataService.submit(pstId, studentId);
        return new JsonResult<>(OK, pstDetailDevice);
    }

    @GetMapping("/group/submit")
    public JsonResult<PSTDetailDevice> groupSubmit(Integer groupId, Integer pstId, HttpSession session){
        Integer studentId = getUserIdFromSession(session);
        PSTDetailDevice pstDetailDevice = deviceDetailDataService.groupSubmit(groupId,pstId,studentId);
        return new JsonResult<>(OK, pstDetailDevice);
    }
}
