package com.iecube.community.model.EMDV4.TopMajorField.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TopMajorFieldServiceTest {

    @Autowired
    private TopMajorFieldService service;

    @Test
    public void getTest(){
        System.out.println(service.getAllMajorField());
    }
}
