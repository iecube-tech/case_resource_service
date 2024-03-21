package com.iecube.community.model.task_attention.mapper;

import com.iecube.community.model.task_attention.entity.Attention;
import com.iecube.community.model.task_attention.entity.TaskAttention;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskAttentionMapper {
    Integer insert(Attention attention);

    Integer connect(TaskAttention taskAttention);

    List<Attention> getAttentionByTaskId(Integer taskId);

    List<Attention> getAttentionByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getAttentionIdByTaskTemplateId(Integer taskTemplateId);

    Integer deleteAttentionById(Integer id);
}
