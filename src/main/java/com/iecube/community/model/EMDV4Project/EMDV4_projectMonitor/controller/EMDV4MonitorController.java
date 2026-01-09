package com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.service.EMDV4MonitorService;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorInfoVo;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorOverview;
import com.iecube.community.model.EMDV4Project.EMDV4_projectMonitor.vo.MonitorStuVo;
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

    @GetMapping("/stu/status/paging")
    public JsonResult<MonitorOverview> getStuMonitorOverview(Integer projectId, Integer page, Integer pageSize, Integer status) {
        return new JsonResult<>(OK, mdv4MonitorService.getStuMonitorPagingWithStatus(projectId, page, pageSize, status));
    }

    @GetMapping("/pt/stu/status/paging")
    public JsonResult<MonitorOverview> getPtMonitorOverview(Integer projectId, Long ptId, Integer page, Integer pageSize, Integer status) {
        return new JsonResult<>(OK, mdv4MonitorService.getTaskStuMonitorPagingWithStatus(projectId, ptId, page, pageSize, status));
    }

    @GetMapping("/stu/search")
    public JsonResult<MonitorOverview> searchStu(Integer projectId, Long ptId, Integer page, Integer pageSize, String keyword ) {
        return new JsonResult<>(OK, mdv4MonitorService.searchStudent(projectId, ptId, page, pageSize, keyword));
    }

    @GetMapping("/ps")
    public JsonResult<MonitorStuVo> getPs(Integer projectId, Long psId) {
        return new JsonResult<>(OK, mdv4MonitorService.getStuTaskMonitor(projectId, psId));
    }
}
