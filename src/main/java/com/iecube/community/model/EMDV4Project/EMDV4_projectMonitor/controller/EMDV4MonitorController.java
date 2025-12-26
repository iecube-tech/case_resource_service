package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.EMDV4MonitorService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emdv4/monitor")
public class EMDV4MonitorController extends BaseController {

    @Autowired
    private EMDV4MonitorService mdv4MonitorService;

    @GetMapping("/info")
    public JsonResult<MonitorInfoVo> getMonitorInfo(Integer projectId) {
        return new JsonResult<>(OK, mdv4MonitorService.getMonitorInfo(projectId));
    }
}
