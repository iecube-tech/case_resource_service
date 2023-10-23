package com.iecube.community.model.analysis.mapper;

import com.iecube.community.model.tag.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AnalysisMapper {
    Integer getProjectNumByCase(Integer caseId);

    List<Integer> getProjectIdListByCaseId(Integer caseId);

    List<Integer> getStudentIdListByProjectId(Integer projectId);

    List<Integer> getProjectStudentScoreList(Integer projectId);

    List<Integer> getTaskTemplateNumListByCase(Integer caseId);

    Integer getTaskIdByProjectAndTaskNum(Integer projectId, Integer taskNum);

    // 获取一个project下的一个task的所以成绩
    List<Integer> getATaskScoreListByTaskId(Integer taskId);

    //获取所有的pstId
    List<Integer> getPSTIdListByProject(Integer projectId);

    List<Integer> getTagIdListByPSTId(Integer pstId);

    String getTagName(Integer tagId);

    String getTagSuggestion(Integer tagId);

    Integer getTaskNumByTag(Integer tagNum);

    Integer getProjectStudentNum(Integer projectId);

    List<Integer> getTaskListByProject(Integer projectId);
}
