package com.iecube.community.model.task_deliverable_requirement.mapper;

import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DeliverableRequirementMapperTest {
    @Autowired
    private DeliverableRequirementMapper deliverableRequirementMapper;

    @Test
    public void getDeliverableRequirementByTestId(){
        System.out.println(deliverableRequirementMapper.getDeliverableRequirementByTaskId(43));
    }
}
