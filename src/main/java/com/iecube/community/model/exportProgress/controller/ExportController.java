package com.iecube.community.model.exportProgress.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.exportProgress.entity.ExportProgress;
import com.iecube.community.model.exportProgress.service.ExportService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/export")
public class ExportController extends BaseController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/test")
    public JsonResult<ExportProgress> test(){
        ExportProgress res = exportService.createExportTask(283, ExportProgress.Types.PROJECT_REPORT_EXPORT.getValue());
        return new JsonResult<>(OK, res);
    }
}
