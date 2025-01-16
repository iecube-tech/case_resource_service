package com.iecube.community.model.elaborate_md.course.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.elaborate_md.course.entity.CourseEntity;
import com.iecube.community.model.elaborate_md.course.mapper.CourseMapper;
import com.iecube.community.model.elaborate_md.course.qo.CourseQo;
import com.iecube.community.model.elaborate_md.course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;


    @Override
    public void createCourse(CourseQo courseQo) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setName(courseQo.getName());
        int res = courseMapper.InsertCourseEntity(courseEntity);
        if(res!=1){
            throw new InsertException("新建数据异常");
        }
    }

    @Override
    public void updateCourse(CourseQo courseQo) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setId(courseQo.getId());
        courseEntity.setName(courseQo.getName());
        int res  = courseMapper.UpCourseEntity(courseEntity);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void deleteCourse(CourseQo courseQo) {
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setId(courseQo.getId());
        courseEntity.setName(courseQo.getName());
        int res = courseMapper.DelCourseEntity(courseEntity);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public List<CourseEntity> allCourses() {
        return courseMapper.GetAll();
    }
}
