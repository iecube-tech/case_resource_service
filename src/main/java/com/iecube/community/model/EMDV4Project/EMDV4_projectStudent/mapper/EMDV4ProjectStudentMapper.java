package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectStudentMapper {
    int insert(EMDV4ProjectStudent record);
    int batchInsert(List<EMDV4ProjectStudent> list);
    List<EMDV4ProjectStudent> getByProjectId(Integer projectId);
    void updateProjectTotalNum(Integer projectId, int totalNumOfLabs, int totalNumOfTags);

}
