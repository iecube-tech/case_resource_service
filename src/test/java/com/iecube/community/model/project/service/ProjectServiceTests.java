package com.iecube.community.model.project.service;

import com.iecube.community.model.project.entity.StudentProjectVo;
import org.apache.poi.ss.usermodel.DataFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class ProjectServiceTests {

    @Autowired
    private ProjectService projectService;

    @Value("${export-report}")
    private String reportPath;

    @Test
    public void projectStudentTasks(){
        System.out.println(projectService.projectStudentAndStudentTasks(12));
    }

    @Test
    public  void StudentProjectDetail(){
        StudentProjectVo studentProjectVo = projectService.studentProjectDetail(12);
        System.out.println(studentProjectVo);
    }

    @Test
    public void mkdirTest(){
        String path = reportPath+"/蓝牙音箱/李四";
        try {
            Path pathCreateFiles = Files.createDirectories(Paths.get(path));
            System.out.println(pathCreateFiles);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void downloadStudentReportTest(){
        File file = projectService.downloadStudentReport(12,35);
        System.out.println(file.getPath());
    }

    @Test
    public void DataFormatTest(){
        Date date = new Date();
        SimpleDateFormat f =new SimpleDateFormat("YYYY-MM-dd");
        System.out.println(f.format(date));
    }

    @Test
    public void downloadProjectReportTest(){
        File file = projectService.downloadProjectReport(12);
        System.out.println(file.getPath());
    }
}
