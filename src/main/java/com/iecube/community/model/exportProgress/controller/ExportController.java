package com.iecube.community.model.exportProgress.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.service.ExportService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
public class ExportController extends BaseController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/report/get/{projectId}")
    public JsonResult<ExportProgress> getReport(@PathVariable Integer projectId){
        ExportProgress res = exportService.create(projectId, ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue(), currentUserId());
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/report/rate/{id}")
    public JsonResult<ExportProgress> getReportRate(@PathVariable String id){
        ExportProgress res = exportService.getExportProgress(id);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/report/cancel/{id}")
    public JsonResult<ExportProgress> cancelReportGen(@PathVariable String id){
        ExportProgress res = exportService.cancelExportTask(id);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/report/regen/{projectId}")
    public JsonResult<ExportProgress> reGenReport(@PathVariable Integer projectId){
        ExportProgress res = exportService.reCreate(projectId, ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue(), currentUserId());
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/grade/get/{projectId}")
    public JsonResult<ExportProgress> genGrade(@PathVariable Integer projectId){
        ExportProgress res = exportService.create(projectId, ExportProgress.Types.PROJECT_GRADE_EXPORT.getValue(), currentUserId());
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/grade/regen/{projectId}")
    public JsonResult<ExportProgress> reGenGrade(@PathVariable Integer projectId){
        ExportProgress res = exportService.reCreate(projectId, ExportProgress.Types.PROJECT_GRADE_EXPORT.getValue(), currentUserId());
        return new JsonResult<>(OK, res);
    }


}
