package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
import com.iecube.community.model.EMDV4Project.EMDV4_component.entity.EMDV4Component;
import com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.entity.EMDV4StudentTaskBook;

import java.util.List;

public interface EMDV4StudentTaskBookService {
    EMDV4StudentTaskBook createStudentTaskBook(BookLabCatalog labProc);

    /**
     * @param taskBookId 实验详情对应的指导书的id
     * @return 完整的实验指导书
     */
    EMDV4StudentTaskBook getByBookId(String taskBookId);

    EMDV4StudentTaskBook updateStatus(String taskBookId, Integer status);

    EMDV4StudentTaskBook updateScore(String taskBookId, Double score);

    EMDV4StudentTaskBook getRootTaskBook(String taskBookLeafId);

    EMDV4StudentTaskBook computeAiScore(String taskBookLeafId);

    EMDV4StudentTaskBook computeCheckScore(String taskBookLeafId);

    List<EMDV4StudentTaskBook> batchGetById(List<String> idList);

    List<EMDV4StudentTaskBook> batchUpdateWeighting(List<EMDV4StudentTaskBook> list);

    EMDV4StudentTaskBook computeTaskBookScore(EMDV4StudentTaskBook node);

    List<EMDV4StudentTaskBook> getProjectTaskBlockList(Long projectTaskId);

    List<EMDV4StudentTaskBook> getAllLeafNodes(EMDV4StudentTaskBook rootNode);

    EMDV4StudentTaskBook updateBlockTime(String blockId, Boolean startTime, Boolean endTime);

    void batchUpdateBlockStatus(EMDV4StudentTaskBook changed);
    void batchUpdateBlockCurrentChild(EMDV4StudentTaskBook changed);
    void batchUpdateBlockScore(EMDV4StudentTaskBook changed);
    void batchUpdateBlockTime(EMDV4StudentTaskBook changed);
    void handleUpdateLevel2StartTime(EMDV4Component component);
}
