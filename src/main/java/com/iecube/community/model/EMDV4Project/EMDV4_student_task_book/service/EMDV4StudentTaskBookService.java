package com.iecube.community.model.EMDV4Project.EMDV4_student_task_book.service;

import com.iecube.community.model.EMDV4.BookLab.entity.BookLabCatalog;
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
}
