package com.iecube.community.model.Exam.vo;

import com.iecube.community.model.Exam.entity.ExamPaper;
import lombok.Data;

import java.util.List;

@Data
public class StuExamPaperVo {
    private ExamInfoVo examInfo;
    private ExamStudentVo examStudent;
    private List<ExamPaper>  examPapers;
    private List<Long> esIdList;
}
