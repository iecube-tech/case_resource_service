package com.iecube.community.model.Exam.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.baseservice.ex.ServiceException;
import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.Exam.entity.ExamInfoEntity;
import com.iecube.community.model.Exam.entity.ExamPaper;
import com.iecube.community.model.Exam.qo.ExamSaveQo;
import com.iecube.community.model.Exam.vo.*;
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
import java.util.Map;

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
        examService.publishExamToProject(qo.getProjectId(), examId);
        return new JsonResult<>(OK);
    }

    @GetMapping("/course")
    public JsonResult<List<ExamCourseVo>> getMyExamCourse(){
        return new JsonResult<>(OK, examService.getExamCourses(currentUserId()));
    }

    @GetMapping("/{projectId}/examList")
    public JsonResult<Map<String, List<ExamInfoVo>>> getCourseExamList(@PathVariable Integer projectId){
        return new JsonResult<>(OK, examService.getCourseExamList(projectId));
    }

    @GetMapping("/examinfo")
    public JsonResult<ExamInfoVo> getExamInfo(Long examId){
        return new JsonResult<>(OK, examService.getExamInfo(examId));
    }

    @DeleteMapping("/del/{projectId}/{examId}")
    public JsonResult<Map<String, List<ExamInfoVo>>> delCourseExam(@PathVariable Integer projectId, @PathVariable Long examId){
        return new JsonResult<>(OK, examService.delCourseExam(projectId, examId));
    }

    @GetMapping("/{examId}/students")
    public JsonResult<List<ExamStudentVo>> getExamStudents(@PathVariable Long examId, Integer page, Integer pageSize){
        return new JsonResult<>(OK, examService.getExamStudentList(examId, page, pageSize));
    }

    @GetMapping("/{esId}/exam_paper")
    public JsonResult<StuExamPaperVo> getExamStudentPaper(@PathVariable Long esId){
        return new JsonResult<>(OK, examService.getStudentExamPaperVo(esId));
    }

    @PostMapping("/{esId}/scoreup")
    public JsonResult<Void> scoreUp(@PathVariable Long esId, String quesId, Boolean upRemark, String remark, Double score){
        examService.upQuesScore(esId,quesId,upRemark,remark, score);
        return new JsonResult<>(OK);
    }

    @GetMapping("/stu/exam_course")
    public JsonResult<List<ExamCourseVo>> getStudentExamCourses(){
        return new JsonResult<>(OK, examService.getStudentExamCourses(currentUserId()));
    }

    @GetMapping("/stu/{projectId}/exam_list")
    public JsonResult<Map<String, List<StuExamInfoVo>>> getExamList(@PathVariable Integer projectId){
        return new JsonResult<>(OK, examService.getCourseExamList(projectId, currentUserId()));
    }

    @GetMapping("/stu/{esId}/paper")
    public JsonResult<StuExamPaperVo> stuGetExamStudentPaper(@PathVariable Long esId){
        return new JsonResult<>(OK, examService.getStudentExamPaperVo(esId, currentUserId()));
    }

    @PostMapping("/stu/{esId}/start_exam")
    public JsonResult<StuExamInfoVo> startExam(@PathVariable Long esId){
        StuExamInfoVo res = examService.startExam(esId, currentUserId());
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/stu/up_ques")
    public JsonResult<Void> upQues(@RequestBody ExamPaper examPaper){
        examService.updateExamPaper(examPaper);
        return new JsonResult<>(OK);
    }
}
