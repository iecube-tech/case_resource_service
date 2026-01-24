package com.iecube.community.model.Exam.Service;

import com.iecube.community.model.Exam.qo.ExamSaveQo;
import com.iecube.community.model.Exam.vo.ExamParseVo;

import java.util.List;

public interface ExamService {
    ExamParseVo parseExcel(Integer projectId, String filename);

    Long savaExam(ExamSaveQo qo, Integer currentUser);

    void publishExam(List<Integer> studentIdList, Long examId);
}
