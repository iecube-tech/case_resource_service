package com.iecube.community.model.design.controller;

import com.iecube.community.basecontroller.design.DesignBaseController;
import com.iecube.community.model.design.qo.CourseDesignQo;
import com.iecube.community.model.design.service.DesignService;
import com.iecube.community.model.design.vo.CaseDesign;
import com.iecube.community.model.design.vo.CourseDesign;
import com.iecube.community.model.design.vo.Design;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/design")
public class DesignController extends DesignBaseController {
    @Autowired
    private DesignService designService;

    @PostMapping("/add_design/{caseId}")
    public JsonResult<CaseDesign> contentAddDesign(@PathVariable Integer caseId, @RequestBody Design design){
        designService.addCaseDesign(caseId,design);
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK,caseDesign);
    }

    @GetMapping("/{caseId}")
    public JsonResult<CaseDesign> getCaseDesigns(@PathVariable Integer caseId){
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK,caseDesign);
    }

    @PostMapping("/delete/{caseId}")
    public JsonResult<CaseDesign> deleteCaseDesign(@PathVariable Integer caseId, Integer caseTargetId){
        designService.deleteCaseDesign(caseTargetId);
        CaseDesign caseDesign = designService.getCaseDesign(caseId);
        return new JsonResult<>(OK, caseDesign);
    }

    @GetMapping("/course_design")
    public JsonResult<List> getCourseDesign(Integer courseId){
        List<CourseDesign> courseDesignList = designService.getCourseDesigns(courseId);
        return new JsonResult<>(OK, courseDesignList);
    }

    @PostMapping("/add_course_design")
    public JsonResult<List> addCourseDesign(Integer courseId, @RequestBody CourseDesignQo courseDesignQo){
        System.out.println(courseId);
        System.out.println(courseDesignQo);
        designService.addCourseDesign(courseId, courseDesignQo);
        List<CourseDesign> courseDesignList = designService.getCourseDesigns(courseId);
        return new JsonResult<>(OK, courseDesignList);
    }

    @GetMapping("/delete_course_design/{courseId}")
    public JsonResult<List> deleteCourseDesign(@PathVariable Integer courseId, Integer id){
        designService.deleteCourseDesign(id);
        List<CourseDesign> courseDesignList = designService.getCourseDesigns(courseId);
        return new JsonResult<>(OK, courseDesignList);
    }
}
