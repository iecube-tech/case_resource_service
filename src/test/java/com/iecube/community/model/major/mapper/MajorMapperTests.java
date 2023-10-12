package com.iecube.community.model.major.mapper;

import com.iecube.community.model.major.entity.ClassAndGrade;
import com.iecube.community.model.major.entity.Major;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MajorMapperTests {

    @Autowired
    private MajorMapper majorMapper;

    @Test
    public void getMajorTest(){
        System.out.println(majorMapper.TeacherMajorClass(6));
    }

    @Test
    public void TeacherMajorId(){
        System.out.println(majorMapper.TeacherMajorId("微电子科学与工", 6));
    }

    @Test
    public void MajorClassId(){
        System.out.println(majorMapper.MajorClassId("2023", "微电子科学与工程2201",1));
    }

    @Test
    public void addGradeClass(){
        ClassAndGrade classAndGrade = new ClassAndGrade();
        classAndGrade.setGrade("2022");
        classAndGrade.setMajorId(1);
        classAndGrade.setName("微电子科学与工程2204");
        classAndGrade.setCreator(6);
        classAndGrade.setCreateTime(new Date());
        classAndGrade.setLastModifiedTime(new Date());
        classAndGrade.setLastModifiedUser(6);
        majorMapper.addGradeClass(classAndGrade);
        System.out.println(classAndGrade.getId());
    }

    @Test
    public  void addMajor(){
        Major major = new Major();
        major.setName("芯片封装");
        major.setCollageId(1);
        major.setCreator(6);
        major.setCreateTime(new Date());
        major.setLastModifiedTime(new Date());
        major.setLastModifiedUser(6);
        majorMapper.addMajor(major);
        System.out.println(major.getId());
    }
}
