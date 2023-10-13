package com.iecube.community.model.taskTemplate.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.entity.TaskTemplate;
import com.iecube.community.model.taskTemplate.mapper.TaskTemplateMapper;
import com.iecube.community.model.taskTemplate.service.TaskTemplateService;
import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_deliverable_requirement.entity.TaskDeliverableRequirement;
import com.iecube.community.model.task_deliverable_requirement.mapper.DeliverableRequirementMapper;
import com.iecube.community.model.task_reference_file.entity.TaskReferenceFile;
import com.iecube.community.model.task_reference_file.mapper.ReferenceFileMapper;
import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_reference_link.entity.TaskReferenceLink;
import com.iecube.community.model.task_reference_link.mapper.ReferenceLinkMapper;
import com.iecube.community.model.task_requirement.entity.Requirement;
import com.iecube.community.model.task_requirement.entity.TaskRequirement;
import com.iecube.community.model.task_requirement.mapper.RequirementMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskTemplateServiceImpl implements TaskTemplateService {

    @Autowired
    private TaskTemplateMapper taskTemplateMapper;

    @Autowired
    private RequirementMapper requirementMapper;

    @Autowired
    private DeliverableRequirementMapper deliverableRequirementMapper;

    @Autowired
    private ReferenceLinkMapper referenceLinkMapper;

    @Autowired
    private ReferenceFileMapper referenceFileMapper;

    @Override
    public void addTaskTemplateToContent(TaskTemplateDto taskTemplateDto, Integer user){
        //判断任务num是不是已经存在，存在则不能添加  1<=num<=5
        TaskTemplate taskTemplate = new TaskTemplate();
        taskTemplate.setContentId(taskTemplateDto.getContentId());
        taskTemplate.setTaskName(taskTemplateDto.getTaskName());
        taskTemplate.setNum(taskTemplateDto.getNum());
        taskTemplate.setTaskCover(taskCover.codeOf(taskTemplateDto.getNum()).getFiled());
        taskTemplate.setCreator(user);
        taskTemplate.setLastModifiedUser(user);
        taskTemplate.setCreateTime(new Date());
        taskTemplate.setLastModifiedTime(new Date());
        Integer row = taskTemplateMapper.insert(taskTemplate);
        if (row != 1){
            throw new InsertException("插入数据异常");
        }
        taskTemplateDto.setId(taskTemplate.getId());
        // 任务要求
        if(taskTemplateDto.getRequirementList().size()>0){
            for (Requirement requirement: taskTemplateDto.getRequirementList()){
                Integer re = requirementMapper.insert(requirement);
                if (re != 1){
                    throw new InsertException("插入数据异常");
                }
                TaskRequirement taskRequirement = new TaskRequirement();
                taskRequirement.setTaskTemplateId(taskTemplateDto.getId());
                taskRequirement.setRequirementId(requirement.getId());
                Integer co = requirementMapper.connect(taskRequirement);
                if(co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务交付物要求
        if(taskTemplateDto.getDeliverableRequirementList().size()>0){
            for(DeliverableRequirement deliverableRequirement : taskTemplateDto.getDeliverableRequirementList()){
                Integer re = deliverableRequirementMapper.insert(deliverableRequirement);
                if(re!=1){
                    throw new InsertException("插入数据异常");
                }
                TaskDeliverableRequirement taskDeliverableRequirement = new TaskDeliverableRequirement();
                taskDeliverableRequirement.setTaskTemplateId(taskTemplateDto.getId());
                taskDeliverableRequirement.setDeliverableRequirementId(deliverableRequirement.getId());
                Integer co = deliverableRequirementMapper.connect(taskDeliverableRequirement);
                if (co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务参考连接
        if(taskTemplateDto.getReferenceFileList().size()>0){
            for(Resource resource: taskTemplateDto.getReferenceFileList()){
                TaskReferenceFile taskReferenceFile= new TaskReferenceFile();
                taskReferenceFile.setTaskTemplateId(taskTemplateDto.getId());
                taskReferenceFile.setReferenceFileId(resource.getId());
                Integer co = referenceFileMapper.connect(taskReferenceFile);
                if(co != 1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
        // 任务参考文件
        if(taskTemplateDto.getReferenceLinkList().size()>0){
            for(ReferenceLink referenceLink:taskTemplateDto.getReferenceLinkList()){
                Integer re = referenceLinkMapper.insert(referenceLink);
                if (re != 1){
                    throw new InsertException("插入数据异常");
                }
                TaskReferenceLink taskReferenceLink = new TaskReferenceLink();
                taskReferenceLink.setTaskTemplateId(taskTemplateDto.getId());
                taskReferenceLink.setReferenceLinkId(referenceLink.getId());
                Integer co = referenceLinkMapper.connect(taskReferenceLink);
                if(co !=1){
                    throw new InsertException("插入数据异常");
                }
            }
        }
    }

    @Override
    public List<TaskTemplateDto> findTaskTemplateByContent(Integer contentId) {
        List<TaskTemplateDto> contentTaskTemplates = taskTemplateMapper.getTaskTemplatesByContentId(contentId);
        for(TaskTemplateDto taskTemplateDto:contentTaskTemplates){
            List<Requirement> requirementList = requirementMapper.getRequirementsByTaskTemplateId(taskTemplateDto.getId());
            List<DeliverableRequirement> deliverableRequirementList = deliverableRequirementMapper.getDeliverableRequirementByTaskTemplateId(taskTemplateDto.getId());
            List<ReferenceLink> referenceLinkList = referenceLinkMapper.getReferenceLinksByTaskTemplateId(taskTemplateDto.getId());
            List<Resource> referenceFileList = referenceFileMapper.getReferenceFilesByTaskTemplateId(taskTemplateDto.getId());
            taskTemplateDto.setRequirementList(requirementList);
            taskTemplateDto.setDeliverableRequirementList(deliverableRequirementList);
            taskTemplateDto.setReferenceLinkList(referenceLinkList);
            taskTemplateDto.setReferenceFileList(referenceFileList);
        }
        return contentTaskTemplates;
    }

    @Override
    public void deleteTaskTemplateByID(Integer taskTemplateId, Integer user) {
        List<Integer> requirements = requirementMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> deliverableRequirements = deliverableRequirementMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        List<Integer> referenceLinks = referenceLinkMapper.getEntityIdByTaskTemplateId(taskTemplateId);
        for(Integer i: requirements){
            requirementMapper.deleteEntityById(i);
        }
        for(Integer i:deliverableRequirements){
            deliverableRequirementMapper.deleteEntityById(i);
        }
        for(Integer i:referenceLinks){
            referenceLinkMapper.deleteEntityById(i);
        }
        requirementMapper.deleteByTaskTemplateId(taskTemplateId);
        deliverableRequirementMapper.deleteByTaskTemplateId(taskTemplateId);
        referenceLinkMapper.deleteByTaskTemplateId(taskTemplateId);
        referenceFileMapper.deleteByTaskTemplateId(taskTemplateId);
        taskTemplateMapper.deleteTaskTemplateById(taskTemplateId);
    }

    @Override
    public void addResourceToTaskTemplate(Integer taskTemplateId, Resource resource) {
        TaskReferenceFile taskReferenceFile= new TaskReferenceFile();
        taskReferenceFile.setTaskTemplateId(taskTemplateId);
        taskReferenceFile.setReferenceFileId(resource.getId());
        Integer co = referenceFileMapper.connect(taskReferenceFile);
        if(co != 1){
            throw new InsertException("插入数据异常");
        }
    }


    protected enum taskCover {
        one(1, "0535d42f6bfb410a99cb1969d4917b97.png"),
        two(2, "e6fd432374d147e39bcce187e72bf27c.png"),
        three(3, "efdec9fe617d42ebb9d32097f1cc0f7d.png"),
        four(4, "8063c692d46b410a9577d5b4d995e015.png"),
        five(5, "093beab33bc841f79107bbcc356faf4e.png");

        private final Integer value;
        private final String filed;

        private taskCover(Integer value, String filed) {
            this.value = value;
            this.filed = filed;
        }

        public Integer getValue() {
            return value;
        }

        public String getFiled() {
            return filed;
        }

        public static taskCover codeOf(int code) {
            for (taskCover prizes : values()) {
                if (prizes.getValue() == code) {
                    return prizes;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
