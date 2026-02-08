package com.iecube.community.model.Exam.mapper;

import com.iecube.community.model.Exam.Dto.ExamWithStudentDto;
import com.iecube.community.model.Exam.entity.*;
import com.iecube.community.model.Exam.vo.ExamCourseVo;
import com.iecube.community.model.Exam.vo.ExamStudentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    int insertExamEntity(ExamInfoEntity examInfoEntity);

    void delExamById(Long id);

    int batchInsertQuestion(List<QuestionEntity> list);

    int batchInsertQuesContent(List<QuesContentEntity> list);

    ExamInfoEntity selectExamWithQuestionsAndContent(@Param("id") Long id);

    List<Integer> selectProjectStudentId(@Param("projectId") Integer projectId);

    int batchInsertExamStudent(List<ExamStudent> list);

    int batchInsertExamPaper(List<ExamPaper> list);

    List<ExamCourseVo> selectTeacherExamCourse(Integer creator);

    List<ExamWithStudentDto> selectExamWithStudentDto(Integer projectId);

    ExamWithStudentDto selectExamWithStudentDtoById(Long examId);

    int deleteExamInfoByExamId(@Param("id") Long id);

    List<ExamStudentVo>  selectExamStudentVoByExamId(@Param("examId") Long examId, Integer offset, Integer pageSize);

    List<Long> selectEsIdByExamId(@Param("examId") Long examId);

    ExamStudentVo selectExamStudentVoByEsId(Long esId);

    List<ExamPaper> selectExamStudentPaper(@Param("esId")Long esId);

    void updateQuesScore(String quesId, Double score);

    void updateQuesAiScore(String quesId, Double aiScore, String payloadStr);

    void updateEsScoreAndRemark(Long esId, Double score ,String remark);

    void updateEsScore(Long esId, Double score);

    void updateEsAiScore(Long esId, Double aiScore);
}
