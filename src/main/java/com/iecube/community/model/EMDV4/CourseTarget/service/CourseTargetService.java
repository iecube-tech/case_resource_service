package com.iecube.community.model.EMDV4.CourseTarget.service;

import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;

import java.util.List;

public interface CourseTargetService {

    List<CourseTarget> getCourseTargetByMF(Long MF);
}
