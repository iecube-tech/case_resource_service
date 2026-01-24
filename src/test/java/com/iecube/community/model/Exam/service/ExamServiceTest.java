package com.iecube.community.model.Exam.service;

import com.iecube.community.model.Exam.Service.ExamService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ExamServiceTest {
    @Autowired
    private ExamService examService;
    @Test
    public void test() {
        examService.publishExam(new ArrayList<>(),1L);
    }
}
