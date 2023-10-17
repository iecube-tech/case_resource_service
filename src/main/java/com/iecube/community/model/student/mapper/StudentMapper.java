package com.iecube.community.model.student.mapper;

import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.entity.Student;
import com.iecube.community.model.student.entity.StudentDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
    StudentDto getById(Integer id);

    Student getByStudentId(String StudentId);

    Student getStudentById(Integer id);

    Integer changePassword(Integer id, String password);

    List<Student> getByEmail(String email);

    Integer addStudent(AddStudentDto addStudentDto);

    Integer deleteStudent(Integer id);

    List<StudentDto> findStudentsLimitByTeacher(Integer teacherId, Integer page, Integer pageSize);

    Integer studentsNum(Integer teacherId);

    List<StudentDto> findAllInStatusByTeacher(Integer teacherId);
}
