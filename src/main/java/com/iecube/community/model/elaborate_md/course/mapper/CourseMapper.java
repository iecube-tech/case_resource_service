package com.iecube.community.model.elaborate_md.course.mapper;

import com.iecube.community.model.elaborate_md.course.entity.CourseEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseMapper {
    int InsertCourseEntity(CourseEntity courseEntity);
    int DelCourseEntity(CourseEntity courseEntity);
    List<CourseEntity> GetAll();
    int UpCourseEntity(CourseEntity courseEntity);
}
