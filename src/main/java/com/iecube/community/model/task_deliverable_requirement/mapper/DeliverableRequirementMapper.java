package com.iecube.community.model.task_deliverable_requirement.mapper;

import com.iecube.community.model.task_deliverable_requirement.entity.DeliverableRequirement;
import com.iecube.community.model.task_deliverable_requirement.entity.TaskDeliverableRequirement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeliverableRequirementMapper {
    Integer insert(DeliverableRequirement deliverableRequirement);

    Integer connect(TaskDeliverableRequirement taskDeliverableRequirement);
    List<DeliverableRequirement> getDeliverableRequirementByTaskId(Integer taskId);

    List<DeliverableRequirement> getDeliverableRequirementByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskId(Integer taskId);

    List<Integer> getEntityIdByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getEntityIdByTaskId(Integer taskId);

    Integer deleteEntityById(Integer id);
}
