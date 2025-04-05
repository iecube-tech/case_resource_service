package com.iecube.community.model.elaborate_md_task.mapper;

import com.iecube.community.model.elaborate_md_task.dto.EMDStuTaskDetailDto;
import com.iecube.community.model.elaborate_md_task.entity.EMDStudentTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDStudentTaskMapper {
    int BatchAdd(List<EMDStudentTask> list);
}
