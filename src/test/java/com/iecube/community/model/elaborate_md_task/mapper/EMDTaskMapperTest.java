package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTaskSection;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskBlockVo;
import com.iecube.community.model.elaborate_md_task.vo.EMDTaskSectionVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class EMDTaskMapperTest {

    @Autowired
    private EMDStudentTaskSectionMapper emdStudentTaskSectionMapper;

    @Autowired
    private EMDStudentTaskMapper emdStudentTaskMapper;

    @Autowired
    private EMDSTSBlockMapper emdSTSBlockMapper;

    @Test
    public void getSectionViListTest(){
        int studentId = 35;
        int taskId = 672;
        List<EMDTaskSectionVo> res = emdStudentTaskSectionMapper.getByST(studentId, taskId);
        System.out.println(res);
    }

    @Test
    public void batchGetBlockVo(){
        List<Long> stsIdList = new ArrayList<>();
        stsIdList.add(143L);
        stsIdList.add(144L);
        stsIdList.add(145L);
        stsIdList.add(146L);
        stsIdList.add(147L);
        stsIdList.add(148L);
        stsIdList.add(149L);
        stsIdList.add(150L);
        stsIdList.add(151L);
        stsIdList.add(152L);
        List<EMDTaskBlockVo> res = emdSTSBlockMapper.batchGetBySTSId(stsIdList);
        System.out.println(res);
    }
}
