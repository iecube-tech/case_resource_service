package com.iecube.community.model.question_bank.mapper;

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

}
