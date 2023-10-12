package com.iecube.community.model.task_requirement.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RequirementMapperTests {
    @Autowired
    private RequirementMapper requirementMapper;

    @Test
    public void getRequirementListByTask(){
        System.out.println(requirementMapper.getRequirementsByTaskId(43));
    }
}
