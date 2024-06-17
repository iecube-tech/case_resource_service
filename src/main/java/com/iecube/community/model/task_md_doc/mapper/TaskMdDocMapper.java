package com.iecube.community.model.task_md_doc.mapper;

import com.iecube.community.model.task_md_doc.entity.TaskMdDoc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMdDocMapper {
    Integer connect(TaskMdDoc taskMdDoc);

    TaskMdDoc getTaskMdDocByTaskTemplateId(Integer templateId);

    TaskMdDoc getTaskMdDocByTask(Integer taskId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);


}
