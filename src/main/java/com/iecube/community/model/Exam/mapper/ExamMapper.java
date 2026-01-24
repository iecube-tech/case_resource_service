package com.iecube.community.model.Exam.mapper;

import com.iecube.community.model.Exam.entity.ExamInfoEntity;
import com.iecube.community.model.Exam.entity.QuesContentEntity;
import com.iecube.community.model.Exam.entity.QuestionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    int insertExamEntity(ExamInfoEntity examInfoEntity);

    int batchInsertQuestion(List<QuestionEntity> list);

    int batchInsertQuesContent(List<QuesContentEntity> list);

    ExamInfoEntity selectExamWithQuestionsAndContent(@Param("id") Long id);

    List<Integer> selectProjectStudentId(@Param("projectId") Integer projectId);


}
