package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.mapper;

import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface EMDV4StudentTaskBookMapper {
    int insert(EMDV4StudentTaskBook record);
    int batchInsert(List<EMDV4StudentTaskBook> records);
    EMDV4StudentTaskBook getById(String id);
    List<EMDV4StudentTaskBook> getByPId(String pid);
    int updateStatus(String id, Integer status);
    int batchUpdateStatus(List<EMDV4StudentTaskBook> records);
    int updateScore(String id, Double score, Boolean passStatus);
    int batchUpdateScore(List<EMDV4StudentTaskBook> records);
    int updateScoreOnly(String id, Double score, Double totalScore);
    int updateCurrentChild(String id, Integer currentChild);
    int batchUpdateCurrentChild(List<EMDV4StudentTaskBook> records);
    EMDV4StudentTaskBook getRootByLeaf(String id);
    EMDV4StudentTaskBook getLevel2ByComponentId(String componentId);
    int batchUpdateWeight(List<EMDV4StudentTaskBook> list);
    List<EMDV4StudentTaskBook> batchGetById(List<String> idList);
    List<EMDV4StudentTaskBook> getProjectTaskBlockList(Long projectTaskId);
    int batchUpdateStartTime(Date startTime, List<String> idList);
    int batchUpdateEndTime(Date endTime, List<String> idList);
}
