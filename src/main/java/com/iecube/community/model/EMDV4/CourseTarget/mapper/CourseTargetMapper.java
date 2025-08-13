package com.iecube.community.model.EMDV4.CourseTarget.mapper;

import com.iecube.community.model.EMDV4.CourseTarget.entity.CourseTarget;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseTargetMapper {

    int insert(CourseTarget record);

    /**
     * 专业方向下的所有的  CourseTarget 毕业要求（level=0）、毕业要求指标点（level=1）、课程目标（level=2）
     * 需要重新构建为多棵树
     * @param MF LONG TopMajorField 的id
     * @return List<CourseTarget>
     */
    List<CourseTarget> getAllTargetByMF(Long MF);

    CourseTarget getById(Long id);

    List<CourseTarget> getCourseTargetByBookId(Long bookId);
}
