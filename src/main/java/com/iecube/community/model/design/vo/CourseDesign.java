package com.iecube.community.model.design.vo;

import com.iecube.community.model.design.entity.CourseChapter;
import com.iecube.community.model.design.entity.CourseTarget;
import com.iecube.community.model.design.entity.GraduationPoint;
import lombok.Data;

import java.util.List;

@Data
public class CourseDesign {
    Integer graduationRequirementId;
    String graduationRequirementName;
    List<GraduationPoint> graduationPointList;
    List<CourseTarget> courseTargetList;
    List<CourseChapter> courseChapterList;
}
