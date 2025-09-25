package com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.entity.EMDV4ProjectStudent;
import com.iecube.community.model.EMDV4Project.EMDV4_projectStudent.vo.EMDV4ProjectStudentVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDV4ProjectStudentMapper {
    int insert(EMDV4ProjectStudent record);
    int batchInsert(List<EMDV4ProjectStudent> list);
    List<EMDV4ProjectStudent> getByProjectId(Integer projectId);
    List<EMDV4ProjectStudentVo> getVoByProjectId(Integer projectId);
    List<EMDV4ProjectStudentVo> getProjectStudentListByPTid(Long projectTaskId);
    void updateProjectTotalNum(Integer projectId, int totalNumOfLabs, int totalNumOfTags);
    EMDV4ProjectStudent getByStudentIdAndProjectId(Integer studentId, Integer projectId);
    EMDV4ProjectStudent getById(Long id);
    int updateScore(Long id, Double score);
}
