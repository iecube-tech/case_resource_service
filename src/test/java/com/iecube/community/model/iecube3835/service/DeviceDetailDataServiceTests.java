package com.iecube.community.model.iecube3835.service;

import com.iecube.community.util.pdf.StudentReportGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class DeviceDetailDataServiceTests {

    @Autowired
    private DeviceDetailDataService deviceDetailDataService;

    @Test
    public void genPdfTest(){
        try{
            StudentReportGenerator studentReportGenerator = new StudentReportGenerator();
            studentReportGenerator.startGenerator();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void genData(){
        deviceDetailDataService.submit(7962,35);
    }


}
