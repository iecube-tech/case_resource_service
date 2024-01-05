package com.iecube.community.model.question_bank.mapper;

import com.iecube.community.model.question_bank.dto.PSTQuestionDto;
import com.iecube.community.model.question_bank.dto.PSTQuestionSolutionDto;
import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.vo.QuestionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionBankMapper {
    Integer addQuestion(Question question);
    Integer addSolution(Solution solution);

    Integer deleteQuestion(Integer questionId);
    Integer deleteSolution(Integer solutionId);

    Integer updateQuestion(Question question);
    Integer updateSolution(Solution solution);

    Question getQuestionById(Integer questionId);
    Solution getSolutionById(Integer solutionId);

    List<Solution> getSolutionByQuestion(Integer questionId);
    List<QuestionVo> getQuestionVoByTaskTemplate(Integer taskTemplateId);

    List<Question> getQuestion1(Integer pstId);
    List<Question> getQuestion2(Integer pstId);
    List<Question> getQuestion3(Integer pstId);

    Integer addPSTQuestion(PSTQuestionDto pstQuestionDto);

    Integer addPSTQuestionSolution(PSTQuestionSolutionDto pstQuestionSolutionDto);

    List<QuestionVo> getPstQuestions(Integer pstId);

    List<Integer> getPstQuestionAnswer(Integer pstQuestionId);

    List<Integer> getPStQuestionRealAnswer(Integer pstQuestionId);

    Integer updatePstQuestionResult(Integer pstQuestionId, Integer result);

    Integer updatePSTObjectiveGrade(Integer pstId, double grade);

    Integer getObjectiveGrade(Integer pstId);
    Integer getObjectiveWeighting(Integer pstId);

    Integer updateObjectiveGradeWeighting(Integer pstId, Integer weighting);

    List<Integer> getTaskObjectiveGrades(Integer pstId);

}
