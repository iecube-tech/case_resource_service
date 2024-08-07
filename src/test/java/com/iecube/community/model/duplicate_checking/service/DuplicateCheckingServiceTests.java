package com.iecube.community.model.duplicate_checking.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DuplicateCheckingServiceTests {

    @Autowired DuplicateCheckingService duplicateCheckingService;

    @Test
    public void DuplicateCheckingByPSTid(){
        duplicateCheckingService.DuplicateCheckingByPSTid(1308);
    }

    @Test
    public void getRepetitiveRateByTask(){
        System.out.println(duplicateCheckingService.getRepetitiveRateByTask(322));
    }

    @Test
    public  void getRepetitiveRateByPstId(){
        System.out.println(duplicateCheckingService.getRepetitiveRateByPstId(1308));
    }
}
