package com.iecube.community.model.EMDV4Project.EMDV4_projectTask.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_projectTask.entity.EMDV4ProjectTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectTaskMapper {
    int insert(EMDV4ProjectTask record);
    int batchInsert(List<EMDV4ProjectTask> list);
    List<EMDV4ProjectTask> getByProjectId(Integer projectId);
    void updateProjectStudentNums(Integer projectId, Integer studentNum);
}
