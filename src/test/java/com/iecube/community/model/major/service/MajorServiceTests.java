package com.iecube.community.model.major.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MajorServiceTests {

    @Autowired
    private MajorService majorService;

    @Test
    public void getSchoolCollageMajorsByTeacher(){
        System.out.println(majorService.getSchoolCollageMajorsByTeacher(6));
    }
}
