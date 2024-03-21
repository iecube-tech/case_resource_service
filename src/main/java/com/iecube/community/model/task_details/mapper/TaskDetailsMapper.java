package com.iecube.community.model.task_details.mapper;

import com.iecube.community.model.task_details.entity.Details;
import com.iecube.community.model.task_details.entity.TaskDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskDetailsMapper {
    Integer insert(Details details);

    Integer connect(TaskDetails taskDetails);

    Details getDetailsByTaskId(Integer taskId);

    Details getDetailsByTaskTemplateId(Integer taskTemplateId);

    Integer deleteByTaskTemplateId(Integer taskTemplateId);

    List<Integer> getDetailsIdByTaskTemplateId(Integer taskTemplateId);

    Integer deleteDetailsById(Integer id);
}
