package com.iecube.community.model.student.mapper;


import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.entity.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class StudentMapperTests {
    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void getById(){
        System.out.println(studentMapper.getById(4));
    }

    @Test
    public void getByEmail(){
        System.out.println(studentMapper.getByEmail("17823522@qq.com"));
    }

    @Test
    public void getStudentById(){
        System.out.println(studentMapper.getStudentById(35));
    }

    @Test
    public void addStudent(){
        AddStudentDto student = new AddStudentDto();
        student.setStudentId("22408070101");
        student.setStudentName("张三");
        student.setStudentClass(1);
        student.setMajorId(1);
        student.setStatus(1);
        student.setPassword("111111");
        student.setSalt("11111");
        System.out.println(studentMapper.addStudent(student));
    }

    @Test
    public void findStudentsLimitByTeacherId(){
        System.out.println(studentMapper.findStudentsLimitByTeacher(6,0,20));
    }

    @Test
    public void StudentNum(){
        System.out.println(studentMapper.studentsNum(0));
    }

    @Test
    public void findAllInStatusByTeacher(){
        System.out.println(studentMapper.findAllInStatusByTeacher(6));
    }

    @Test
    public void findByStudentId(){
        System.out.println(studentMapper.getByStudentId("22408070102"));
    }
}
