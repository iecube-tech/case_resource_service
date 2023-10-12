package com.iecube.community.model.task_requirement.mapper;

import com.iecube.community.model.task_requirement.entity.Requirement;
import com.iecube.community.model.task_requirement.entity.TaskRequirement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RequirementMapper {
    List<Requirement> getRequirementsByTaskId(Integer taskId);
    Integer insert(Requirement requirement);
    List<Requirement> getRequirementsByTaskTemplateId(Integer taskTemplateId);
    Integer connect(TaskRequirement taskRequirement);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskId(Integer taskId);

    List<Integer> getEntityIdByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getEntityIdByTaskId(Integer taskId);

    Integer deleteEntityById(Integer id);
}
