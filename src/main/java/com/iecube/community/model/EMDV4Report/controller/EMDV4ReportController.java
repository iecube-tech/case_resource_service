package com.iecube.community.model.EMDV4Report.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.EMDV4Report.entity.ReportTempChapter;
import com.iecube.community.model.EMDV4Report.qo.ReportTempQo;
import com.iecube.community.model.EMDV4Report.service.EMDV4ReportService;
import com.iecube.community.model.resource.service.ResourceService;
import com.iecube.community.util.DownloadUtil;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/emdv4/report")
public class EMDV4ReportController extends BaseController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EMDV4ReportService reportService;

    private final Resource examTemplate = new ClassPathResource("templates/exam_template.xlsx");

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response){
        try {
            DownloadUtil.httpDownload(this.examTemplate.getInputStream(), "实验报告模版.xlsx", response);
        } catch (IOException e) {
            throw new ServiceException("下载异常,",e);
        }
    }

    @PostMapping("/upload")
    public JsonResult<com.iecube.community.model.resource.entity.Resource> upload(MultipartFile file) throws IOException {
        Integer creator = currentUserId();
        if(file.getOriginalFilename()==null || !file.getOriginalFilename().endsWith(".xlsx")){
            throw new ServiceException("仅支持.xlsx文件");
        }
        com.iecube.community.model.resource.entity.Resource resource = resourceService.UploadFile(file,creator);
        return new JsonResult<>(OK, resource);
    }

    @PostMapping("/parse")
    public JsonResult<List<ReportTempChapter>> tempExcelParse(Integer projectId, String filename){
        return new JsonResult<>(OK, reportService.parseChapterFromExcel(projectId,filename));
    }

    @PostMapping("/temp/save")
    public JsonResult<Void> reportTempSave(@RequestBody ReportTempQo reportTempQo){
        return new JsonResult<>(OK);
    }

}
