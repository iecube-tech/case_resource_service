package com.iecube.community.model.taskTemplate.service;

import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_requirement.entity.Requirement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class taskTemplateServiceTests {
    @Autowired
    private TaskTemplateService taskTemplateService;

    @Test
    public void addTaskTemplateToContent(){
        TaskTemplateDto taskTemplateDto = new TaskTemplateDto();
        taskTemplateDto.setContentId(2);
        taskTemplateDto.setTaskCover("e6fd432374d147e39bcce187e72bf27c.png");
        taskTemplateDto.setTaskName("功放电路设计");
        taskTemplateDto.setNum(2);
        List<Requirement> requirementList = new ArrayList<>();
        Requirement requirement1 = new Requirement();
        requirement1.setName("OTL功放电路结构");
        Requirement requirement2 = new Requirement();
        requirement2.setName("设计要求");
        Requirement requirement3 = new Requirement();
        requirement3.setName("设计思路");
        requirementList.add(requirement1);
        requirementList.add(requirement2);
        requirementList.add(requirement3);
        List<DeliverableRequirement> deliverableRequirementList = new ArrayList<>();
        DeliverableRequirement deliverableRequirement1 = new DeliverableRequirement();
        deliverableRequirement1.setName("功放电路设计过程");
        DeliverableRequirement deliverableRequirement2 = new DeliverableRequirement();
        deliverableRequirement2.setName(" 电路仿真、测试");
        DeliverableRequirement deliverableRequirement3 = new DeliverableRequirement();
        deliverableRequirement3.setName("功放管选择");
        deliverableRequirementList.add(deliverableRequirement1);
        deliverableRequirementList.add(deliverableRequirement2);
        deliverableRequirementList.add(deliverableRequirement3);
        List<ReferenceLink> referenceLinkList = new ArrayList<>();
        ReferenceLink referenceLink1 = new ReferenceLink();
        referenceLink1.setName("baidu");
        referenceLink1.setUrl("www.baidu.com");
        ReferenceLink referenceLink2 = new ReferenceLink();
        referenceLink2.setName("biying");
        referenceLink2.setUrl("www.biying.com");
        referenceLinkList.add(referenceLink1);
        referenceLinkList.add(referenceLink2);
        List<Resource> referenceFileList = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setId(117);
        Resource resource2 = new Resource();
        resource2.setId(121);
        referenceFileList.add(resource1);
        referenceFileList.add(resource2);
        taskTemplateDto.setRequirementList(requirementList);
        taskTemplateDto.setDeliverableRequirementList(deliverableRequirementList);
        taskTemplateDto.setReferenceFileList(referenceFileList);
        taskTemplateDto.setReferenceLinkList(referenceLinkList);
        taskTemplateService.addTaskTemplateToContent(taskTemplateDto,0);
    }

    @Test
    public void getTaskTemplates(){
        List<TaskTemplateDto> taskTemplateDtos = taskTemplateService.findTaskTemplateByContent(2);
        System.out.println(taskTemplateDtos);
    }

}
