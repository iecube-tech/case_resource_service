package com.iecube.community.model.taskTemplate.mapper;

import com.iecube.community.model.taskTemplate.dto.TaskTemplateDto;
import com.iecube.community.model.taskTemplate.entity.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskTemplateMapper {
    Integer insert(TaskTemplate taskTemplate);

    Integer update(TaskTemplate taskTemplate);
    List<TaskTemplateDto> getTaskTemplatesByContentId(Integer contentId);

    Integer deleteTaskTemplateById(Integer id);

}
