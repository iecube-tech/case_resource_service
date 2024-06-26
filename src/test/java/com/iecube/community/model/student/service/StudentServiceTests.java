package com.iecube.community.model.student.service;

import com.iecube.community.model.student.dto.AddStudentDto;
import com.iecube.community.model.student.qo.AddStudentQo;
import com.iecube.community.util.SHA256;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PublicKey;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class StudentServiceTests {
    @Autowired StudentService studentService;

    @Test
    public void addStudent(){
        AddStudentQo addStudentQo = new AddStudentQo();
        addStudentQo.setStudentId("22408070133");
        addStudentQo.setStudentName("张小龙");
        addStudentQo.setEmail("1782352276@qq.com");
        addStudentQo.setStudentClass(1);
        addStudentQo.setMajorId(1);
        studentService.addStudent(addStudentQo, 6);
    }

    @Test
    public void passwordSHA256(){
        String pas = "111111";
        System.out.println(SHA256.encryptStringWithSHA256(pas));
    }



}
