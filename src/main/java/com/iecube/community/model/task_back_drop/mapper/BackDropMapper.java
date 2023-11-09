package com.iecube.community.model.task_back_drop.mapper;


import com.iecube.community.model.task_back_drop.entity.BackDrop;
import com.iecube.community.model.task_back_drop.entity.TaskBackDrop;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BackDropMapper {
    Integer insert(BackDrop backDrop);

    Integer connect(TaskBackDrop taskBackDrop);
    List<BackDrop> getBackDropByTaskId(Integer taskId);

    List<BackDrop> getBackDropByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskId(Integer taskId);

    List<Integer> getEntityIdByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getEntityIdByTaskId(Integer taskId);

    Integer deleteEntityById(Integer id);
}
