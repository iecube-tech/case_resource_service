package com.iecube.community.model.teacher.service;

import com.iecube.community.model.auth.dto.LoginDto;
import com.iecube.community.model.teacher.entity.Tags;
import com.iecube.community.model.teacher.entity.Teacher;
import com.iecube.community.model.teacher.vo.TeacherVo;

import java.util.List;

public interface TeacherService {
    LoginDto login(String email, String password);
    void insert(Teacher user);

    void changePassword(Integer teacherId, String oldPassword, String newPassword);

    Teacher teacherAccount(Integer teacherId);

    List<TeacherVo> getTeacherVoList();

    TeacherVo teacherInfo(Integer teacherId);

    void teacherEnable(Integer teacherId);

    void teacherDisable(Integer teacherId);

    List<Teacher> collageTeachers(Integer teacherId);

}
