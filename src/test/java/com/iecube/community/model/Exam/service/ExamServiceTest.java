package com.iecube.community.model.Exam.service;

import com.iecube.community.model.Exam.Service.ExamService;
import com.iecube.community.model.project.service.ProjectService;
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

    @Autowired
    private ProjectService projectService;

    @Test
    public void test() {
        examService.publishExam(new ArrayList<>(),1L);
    }

    @Test
    public void test2(){
        examService.getCourseExamList(246);
    }

    @Test
    public void test3(){
        System.out.println(examService.getExamCourses(13));
    }

}
