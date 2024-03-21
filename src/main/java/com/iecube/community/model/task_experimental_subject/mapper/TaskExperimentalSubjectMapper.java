package com.iecube.community.model.task_experimental_subject.mapper;

import com.iecube.community.model.task_experimental_subject.entity.ExperimentalSubject;
import com.iecube.community.model.task_experimental_subject.entity.TaskExperimentalSubject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskExperimentalSubjectMapper {
    Integer insert(ExperimentalSubject experimentalSubject);

    Integer connect(TaskExperimentalSubject taskExperimentalSubject);

    List<ExperimentalSubject> getExperimentalSubjectByTaskId(Integer taskId);

    List<ExperimentalSubject> getExperimentalSubjectByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getExperimentalSubjectIdByTaskTemplateId(Integer taskTemplateId);

    Integer deleteExperimentalSubjectById(Integer id);
}
