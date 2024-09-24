package com.iecube.community.model.student.service;

import com.iecube.community.model.auth.dto.LoginDto;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;

import java.io.InputStream;
import java.util.List;

public interface StudentService {
    List<StudentDto> findStudentsLimitByTeacherId(Integer teacherId, Integer page, Integer pageSize);

    Integer studentsNum(Integer teacherId);

    List<StudentDto> findAllInStatusByTeacher(Integer teacherId);

    LoginDto jwtLogin(String studentId, String password);

    StudentDto my(Integer studentId);

    void changePassword(Integer studentId, String oldPassword, String newPassword);

    void addStudent(AddStudentQo addStudentQo, Integer teacherId);

    void importByExcel(InputStream in, Integer teacherId);

    void deleteStudentById(List<Integer> studentIds);

}
