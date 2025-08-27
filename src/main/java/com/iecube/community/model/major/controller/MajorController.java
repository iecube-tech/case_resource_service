package com.iecube.community.model.major.controller;

import com.iecube.community.basecontroller.major.MajorBaseController;
import com.iecube.community.model.major.entity.School;
import com.iecube.community.model.major.service.MajorService;
import com.iecube.community.model.major.vo.CollageListOfSchool;
import com.iecube.community.model.major.vo.MajorClass;
import com.iecube.community.model.major.vo.SchoolCollage;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/major")
public class MajorController extends MajorBaseController {
    @Autowired
    private MajorService majorService;

    @GetMapping("/classes")
    public JsonResult<List<MajorClass>> teacherMajorClasses(){
        Integer teacherId = currentUserId();
        List<MajorClass> majorClasses = majorService.teacherMajorClasses(teacherId);
        return new JsonResult<>(OK, majorClasses);
    }

    @GetMapping("/account")
    public JsonResult<SchoolCollage> accountCollage(){
        Integer teacherId = currentUserId();
        SchoolCollage schoolCollage = majorService.teacherCollage(teacherId);
        return new JsonResult<>(OK, schoolCollage);
    }

    @GetMapping("/school_list")
    public JsonResult<List> schoolList(){
        List<School> schoolList = majorService.schoolList();
        return new JsonResult<>(OK, schoolList);
    }

    @GetMapping("/school_collage")
    public JsonResult<List> schoolCollageList(){
        List<CollageListOfSchool> collageListOfSchools = majorService.collageListOfSchoolList();
        return new JsonResult<>(OK, collageListOfSchools);
    }
}
