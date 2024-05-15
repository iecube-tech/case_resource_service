package com.iecube.community.model.taskTemplate.service;

import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;

import java.util.List;

public interface TaskTemplateService {
void addTaskTemplateToContent(TaskTemplateDto taskTemplateDto, Integer user);

void updateTaskTemplate(TaskTemplateDto taskTemplateDto, Integer user);

List<TaskTemplateDto> findTaskTemplateByContent(Integer contentId);

void deleteTaskTemplateByID(Integer taskTemplateId, Integer user);

void addResourceToTaskTemplate(Integer taskTemplateId, Resource resource);
}
