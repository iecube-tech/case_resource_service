package com.iecube.community.model.elaborate_md.course.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md.course.entity.CourseEntity;
import com.iecube.community.model.elaborate_md.course.qo.CourseQo;
import com.iecube.community.model.elaborate_md.course.service.CourseService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elaborate/md/course")
public class CourseController extends BaseController {
    @Autowired
    private CourseService courseService;

    @GetMapping("/all")
    public JsonResult<List<CourseEntity>> getAllCourse() {
        return new JsonResult<>(OK,courseService.allCourses());
    }

    @PostMapping("/add")
    public JsonResult<List<CourseEntity>> addCourse(@RequestBody CourseQo courseQo) {
        courseService.createCourse(courseQo);
        return new JsonResult<>(OK,courseService.allCourses());
    }

    @DeleteMapping("/del")
    public JsonResult<List<CourseEntity>> delCourse(@RequestBody CourseQo courseQo) {
        courseService.deleteCourse(courseQo);
        return new JsonResult<>(OK,courseService.allCourses());
    }

    @PostMapping("/up")
    public JsonResult<List<CourseEntity>> upCourse(@RequestBody CourseQo courseQo) {
        courseService.updateCourse(courseQo);
        return new JsonResult<>(OK,courseService.allCourses());
    }
}
