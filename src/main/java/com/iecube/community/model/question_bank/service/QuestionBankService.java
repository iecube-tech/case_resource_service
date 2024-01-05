package com.iecube.community.model.question_bank.service;

import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.qo.SubmitQo;
import com.iecube.community.model.question_bank.vo.PSTGW;
import com.iecube.community.model.question_bank.vo.QuestionVo;

import java.util.List;

public interface QuestionBankService {

    void taskTemplateAddQuestion(Question question);

    List<QuestionVo> taskTemplateAddQuestionVo(QuestionVo questionVo);

    List<QuestionVo> taskTemplateUpdateQuestionVo(QuestionVo questionVo);

    void questionAddSolution(Solution solution);

    void updateQuestion(Question question);

    void updateSolution(Solution solution);

    void deleteSolution(Integer solutionId);

    void deleteQuestion(Integer questionId);

    List<QuestionVo> getTaskTemplateQuestions(Integer taskTemplateId);

    // 学生端
    List<QuestionVo> getQuestions(Integer pstId);

    List<QuestionVo> submitQuestion(List<SubmitQo> submitQoList, Integer pstId);

    PSTGW getObjectiveGradeAndWeighting(Integer pstId);

    PSTGW updateObjectiveGradeWeighting(Integer pstId, Integer weighting);
}