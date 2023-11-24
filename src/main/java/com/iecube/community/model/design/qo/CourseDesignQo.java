package com.iecube.community.model.design.qo;

import com.iecube.community.model.design.entity.CourseChapter;
import com.iecube.community.model.design.entity.CourseTarget;
import com.iecube.community.model.design.entity.GraduationPoint;
import lombok.Data;

import java.util.List;

@Data
public class CourseDesignQo {
    String graduationRequirementName;
    List<GraduationPoint> graduationPointList;
    List<CourseTarget> courseTargetList;
    List<CourseChapter> courseChapterList;
}
