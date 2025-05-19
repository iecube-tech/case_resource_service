package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.dto.EMDStuTaskDetailDto;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import com.iecube.community.model.task.entity.StudentTaskVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface EMDStudentTaskMapper {
    int BatchAdd(List<EMDStudentTask> list);

    List<StudentTaskVo> emdCourseStudentTaskVo(Integer projectId);

    int updateStatus(Integer studentId, Integer taskId, Integer status);

    void updateStartTime(Integer studentId, Integer taskId, Date startTime);

    void updateEndTime(Integer studentId, Integer taskId, Date endTime);

    EMDStudentTask getByStudentIdTaskId(Integer studentId, Integer taskId);
}
