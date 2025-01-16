package com.iecube.community.model.student.service;

import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.dto.LoginDto;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.model.student.qo.SignInQo;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.InputStream;
import java.util.List;

public interface StudentService {
    List<StudentDto> findStudentsLimitByTeacherId(Integer teacherId, Integer page, Integer pageSize);

    Integer studentsNum(Integer teacherId);

    List<StudentDto> findAllInStatusByTeacher(Integer teacherId);

    StudentDto my(Integer studentId);

    void addStudent(AddStudentQo addStudentQo, Integer teacherId);

    List<AddStudentDto> importByExcel(InputStream in, Integer teacherId);

    void deleteStudentById(List<Integer> studentIds);

    LoginDto jwtLogin(String studentId, String password);

    void changePassword(Integer studentId, String oldPassword, String newPassword);

    void sendSignInCodeToEmail(String email, StringRedisTemplate stringRedisTemplate);

    LoginDto signIn(SignInQo signInQo);

    void sendEmail();
}
