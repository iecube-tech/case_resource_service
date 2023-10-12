package com.iecube.community.model.task.mapper;

import com.iecube.community.model.task.entity.ProjectStudentTaskQo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest

// @RunWith 表示启动这个单元测试类， 需要传递一个参数 必须是SpringRunner的实列类型
@RunWith(SpringRunner.class)
public class TaskMapperTests {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void findStudentTaskByProjectId(){
        System.out.println(taskMapper.findStudentTaskByProjectId(12,35));
    }

//    @Test
//    public void teacherModifyPST(){
//        ProjectStudentTaskQo projectStudentTaskQo = new ProjectStudentTaskQo();
//        projectStudentTaskQo.setPSTid(299);
//        projectStudentTaskQo.setGrade(74);
//        List tags = new ArrayList<>();
//        tags.add("电路设计不合理");
//        tags.add("测试项目不全");
//        projectStudentTaskQo.setTags(tags);
//        System.out.println(taskMapper.TeacherModifyProjectStudentTask(projectStudentTaskQo));
//    }

    @Test
    public void findSuggestionByTagName(){
        System.out.println(taskMapper.findSuggestionByTagName("电路技术指标不正确"));
    }

    @Test
    public void findStudentTaskByPSTId(){
        System.out.println(taskMapper.findStudentTaskByPSTId(171));
    }

    @Test
    public void findTasksByProjectId(){
        System.out.println(taskMapper.findByProjectId(12));
    }

    @Test
    public void updatePSTStatus(){
        System.out.println(taskMapper.updatePSTStatus(171,1));
    }
}
