package com.iecube.community.model.elaborate_md.course.service;

import com.iecube.community.model.elaborate_md.course.entity.CourseEntity;
import com.iecube.community.model.elaborate_md.course.qo.CourseQo;

import java.util.List;

public interface CourseService {
    void createCourse(CourseQo courseQo);

    void updateCourse(CourseQo courseQo);

    void deleteCourse(CourseQo courseQo);

    List<CourseEntity> allCourses();
}
