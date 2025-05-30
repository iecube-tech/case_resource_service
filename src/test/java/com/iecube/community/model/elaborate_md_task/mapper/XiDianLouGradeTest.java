package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.service.EMDTaskService;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskBlockVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskDetailVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskModelVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import com.iecube.community.model.project.entity.ProjectStudentVo;
import com.iecube.community.model.project.service.ProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class XiDianLouGradeTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EMDTaskService emdTaskService;

    // 使用线上数据库 projectId=162
    // 1.获取学生列表
    // 2.获取分组列表
    // 3.分组的组长列表
    // 4.计算组长的成绩
    @Test
    public void outGrade() {
        List<ProjectStudentVo> students = projectService.projectStudentAndStudentTasks(162);
        students.forEach(stu->{
            EMDTaskDetailVo taskDetailVo = emdTaskService.getTaskDetailVo(915, stu.getId());
            List<EMDTaskModelVo> labModelVoList = taskDetailVo.getLabModelVoList();
            labModelVoList.forEach(labModelVo->{
                List<EMDTaskSectionVo> sectionVoList = labModelVo.getSectionVoList();
                sectionVoList.forEach(sectionVo->{
                    List<EMDTaskBlockVo> blockList = sectionVo.getBlockVoList();
                    for (EMDTaskBlockVo blockVo : blockList) {
                        if(blockVo.getType().equals("TEXT")){
                            break;
                        }

                    }
                });
            });
        });
    }
}
