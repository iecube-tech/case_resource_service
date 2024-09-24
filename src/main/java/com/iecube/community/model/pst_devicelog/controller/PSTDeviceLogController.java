package com.iecube.community.model.pst_devicelog.controller;

import com.iecube.community.basecontroller.pst_devicelog.PSTDeviceLogBaseController;
import com.iecube.community.model.pst_devicelog.dto.PSTDeviceLogParseDto;
import com.iecube.community.model.pst_devicelog.dto.StudentLogOverview;
import com.iecube.community.model.pst_devicelog.entity.PSTDeviceLog;
import com.iecube.community.model.pst_devicelog.entity.TaskInfo;
import com.iecube.community.model.pst_devicelog.service.PSTDeviceLogService;
import com.iecube.community.model.pst_devicelog.vo.StudentTasksOperations;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import com.iecube.community.util.LogParsing.LogParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/dlog")
public class PSTDeviceLogController extends PSTDeviceLogBaseController {

    @Value("${resource-location}/file")
    private String files;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PSTDeviceLogService pstDeviceLogService;

    @PostMapping("/upload/{pstId}")
    public JsonResult<PSTDeviceLog> uploadPSTDeviceLog( MultipartFile file, @PathVariable Integer pstId) throws IOException {
        Integer creator = currentUserId();
        Resource resource = resourceService.UploadFile(file, creator);
        PSTDeviceLog pstDeviceLog = pstDeviceLogService.uploadGroupDeviceLog(pstId, resource);
        return new JsonResult<>(OK, pstDeviceLog);
    }

    @GetMapping("/logs/{pstId}")
    public JsonResult<List> getPSTDeviceLogs(@PathVariable Integer pstId){
        List<Resource> pstDeviceLogs = pstDeviceLogService.getPSTDeviceLogsByPstId(pstId);
        return new JsonResult<>(OK, pstDeviceLogs);
    }

    @GetMapping("/logs/download/{filename}")
    public void getLog(@PathVariable String fileName, HttpServletResponse response){
        Resource resource = resourceService.getResourceByFilename(fileName);
        DownloadUtil.httpDownload(new File(this.files, fileName), resource.getOriginFilename(), response);
    }

    @GetMapping("/pstinfo/{pstId}")
    public JsonResult<TaskInfo> getTaskDetailByPSTId(@PathVariable Integer pstId){
        TaskInfo taskInfo = pstDeviceLogService.getTaskDetailByPSTId(pstId);
        return new JsonResult<>(OK, taskInfo);
    }

    @GetMapping("/echarts/{pstId}")
    public JsonResult<PSTDeviceLogParseDto> getLogVisualization(@PathVariable Integer pstId){
        PSTDeviceLogParseDto pstDeviceLogParseDto = pstDeviceLogService.getLogVisualization(pstId);
        return new JsonResult<>(OK, pstDeviceLogParseDto);
    }

    @GetMapping("/compare/{projectId}")
    public JsonResult<List> getProjectLogCompare(@PathVariable Integer projectId){
        List<StudentTasksOperations> result = pstDeviceLogService.getProjectLogCompare(projectId);
        return new JsonResult<>(OK, result);
    }
}
