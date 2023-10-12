package com.iecube.community.model.task_reference_file.mapper;

import com.iecube.community.model.resource.entity.Resource;
import com.iecube.community.model.task_reference_file.entity.TaskReferenceFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReferenceFileMapper {
    List<Resource> getReferenceFilesByTaskId(Integer taskId);

    List<Resource> getReferenceFilesByTaskTemplateId(Integer taskTemplateId);

    Integer connect(TaskReferenceFile taskReferenceFile);

    List<Integer> getResourceIdByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getResourceIdByTaskId(Integer taskId);
    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskId(Integer taskId);

}
