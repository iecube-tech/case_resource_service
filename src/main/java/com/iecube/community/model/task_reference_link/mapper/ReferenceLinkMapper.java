package com.iecube.community.model.task_reference_link.mapper;

import com.iecube.community.model.task_reference_link.entity.ReferenceLink;
import com.iecube.community.model.task_reference_link.entity.TaskReferenceLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReferenceLinkMapper {
    List<ReferenceLink> getReferenceLinksByTaskId(Integer taskId);

    List<ReferenceLink> getReferenceLinksByTaskTemplateId(Integer taskTemplateId);

    Integer insert(ReferenceLink referenceLink);

    Integer connect(TaskReferenceLink taskReferenceLink);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskId(Integer taskId);

    List<Integer> getEntityIdByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getEntityIdByTaskId(Integer taskId);

    Integer deleteEntityById(Integer id);
}
