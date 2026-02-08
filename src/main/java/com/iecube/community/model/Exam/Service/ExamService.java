package com.iecube.community.model.Exam.Service;

import com.iecube.community.model.Exam.entity.ExamPaper;
import com.iecube.community.model.Exam.qo.ExamSaveQo;
import com.iecube.community.model.Exam.vo.*;

import java.util.List;
import java.util.Map;

public interface ExamService {
    ExamParseVo parseExcel(Integer projectId, String filename);

    Long savaExam(ExamSaveQo qo, Integer currentUser);

    void publishExam(List<Integer> studentIdList, Long examId);

    void publishExamToProject(Integer projectId, Long examId);

    List<ExamCourseVo> getExamCourses(Integer creator);

    Map<String, List<ExamInfoVo>> getCourseExamList(Integer projectId);

    ExamInfoVo getExamInfo(Long examId);

    Map<String, List<ExamInfoVo>> delCourseExam(Integer projectId, Long examId);

    List<ExamStudentVo> getExamStudentList(Long examId, int page, int pageSize);

    StuExamPaperVo getStudentExamPaperVo(Long esId);

    void upQuesScore(Long esId, String quesId, Boolean upRemark, String remark, Double score);
    // todo 计算成绩
}
