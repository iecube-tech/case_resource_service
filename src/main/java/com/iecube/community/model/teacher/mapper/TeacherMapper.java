package com.iecube.community.model.teacher.mapper;

import com.iecube.community.model.teacher.entity.Tags;
import com.iecube.community.model.teacher.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface TeacherMapper {
    Teacher findByEmail(String email);

    Teacher findById(Integer id);

    Integer insert(Teacher teacher);

    List<Teacher> findAll();

    Integer changePassword(String password, Integer id);

    Integer teacherEnable(Integer teacherId);

    Integer teacherDisable(Integer teacherId);

    List<Teacher> collageTeachers(Integer teacherId);

    List<Teacher> courseTeacher(Integer courseId);
}
