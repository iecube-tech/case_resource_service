package com.iecube.community.model.Exam.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.Exam.qo.ExamSaveQo;
import com.iecube.community.model.Exam.vo.ExamParseVo;
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

@RestController
@RequestMapping("/exam")
public class ExamController extends BaseController {

    @Autowired
    private ExamService examService;

    @Autowired
    private ResourceService resourceService;

    private final Resource examTemplate = new ClassPathResource("templates/exam_template.xlsx");

    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) {
        try {
            DownloadUtil.httpDownload(this.examTemplate.getInputStream(), "考试信息模版.xlsx", response);
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
    public JsonResult<ExamParseVo> testExcelParse(Integer projectId, String filename){
        ExamParseVo res = examService.parseExcel(projectId, filename);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/save")
    public JsonResult<Void> saveExam(@RequestBody ExamSaveQo qo){
        Long examId = examService.savaExam(qo, currentUserId());
        return new JsonResult<>(OK);
    }
}
