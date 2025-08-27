package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectStudentTaskMapper {
    int insert(EMDV4ProjectStudentTask record);
    int batchInsert(List<EMDV4ProjectStudentTask> records);
}
