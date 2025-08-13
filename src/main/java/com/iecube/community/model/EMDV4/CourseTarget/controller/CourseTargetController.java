package com.iecube.community.model.EMDV4.CourseTarget.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;
import com.iecube.community.model.EMDV4.CourseTarget.service.CourseTargetService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emdv4/course_target")
public class CourseTargetController extends BaseController {

    @Autowired
    private CourseTargetService courseTargetService;

    @GetMapping("/mf_course_target")
    public JsonResult<List<CourseTarget>> getCourseTargetByMF(Long MF){
        return new JsonResult<>(OK, courseTargetService.getCourseTargetByMF(MF));
    }
}
