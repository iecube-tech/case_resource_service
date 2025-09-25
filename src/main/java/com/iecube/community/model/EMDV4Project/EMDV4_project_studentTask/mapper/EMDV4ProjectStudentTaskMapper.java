package com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_project_studentTask.entity.EMDV4ProjectStudentTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface EMDV4ProjectStudentTaskMapper {
    int insert(EMDV4ProjectStudentTask record);
    int batchInsert(List<EMDV4ProjectStudentTask> records);
    EMDV4ProjectStudentTask getByPTIdAndPSId(Long projectTaskId, Long projectStudentId);
    List<EMDV4ProjectStudentTask> getByProjectStudent(Long projectStudent);
    EMDV4ProjectStudentTask getById(Long id);
    EMDV4ProjectStudentTask getByPSIdAndPTNum(Long projectStudentId, Integer projectTaskNum);
    int updateAiScore(String taskBookId, double aiScore, Date aiScoreTime);
    int updateScore(String taskBookId, double score, double totalScore);
    int batchUpdateScore(List<EMDV4ProjectStudentTask> list);
    int updateCheckScore(Long id, double score);
    EMDV4ProjectStudentTask getByTaskBookId(String taskBookId);
    List<EMDV4ProjectStudentTask> getByPTAndStuIdList(Long ptId, List<Integer> list);
    int batchUpdateStartTime(List<Long> idList, Date startTime);
    int batchUpdateDoneTime(List<Long> idList, Date doneTime);
    int updateStatus(Long id, Integer status);
}
