package com.iecube.community.model.project.mapper;

import com.iecube.community.model.project.entity.Project;
import com.iecube.community.model.project.mapper.ProjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)

public class ProjectMapperTests {

    @Autowired
    private ProjectMapper projectMapper;

    @Test
    public void findById(){
        System.out.println(projectMapper.findById(12));
    }

    @Test
    public void addProject(){
        Project project = new Project();
        project.setProjectName("蓝牙音箱");
        project.setCover("230710183916蓝牙音箱.jpg");
        project.setIntroduction("11111");
        project.setIntroduce("11111");
        project.setTarget("111111");
        project.setStartTime(new Date());
        project.setEndTime(new Date());
        projectMapper.insert(project);
    }

    @Test
    public void findByCreator(){
        System.out.println(projectMapper.findByCreator(6));
    }

    @Test
    public void findStudentsByProjectId(){
        System.out.println(projectMapper.findStudentsByProjectId(12));
    }

    @Test
    public void findProjectByStudentId(){
        System.out.println(projectMapper.findByStudentId(35));
    }

    @Test
    public void  findProjectStudent(){
        System.out.println(projectMapper.findProjectStudent(12,35));
    }

    @Test
    public  void updateProjectStudentGrade(){
        projectMapper.updateProjectStudentGrade(42, 83);
    }
}
