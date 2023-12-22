package com.iecube.community.model.question_bank.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.mapper.QuestionBankMapper;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.vo.QuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {
    @Autowired
    private QuestionBankMapper questionBankMapper;

    @Override
    public void taskTemplateAddQuestion(Question question){
        Integer res = questionBankMapper.addQuestion(question);
        if(res!=1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public List<QuestionVo> taskTemplateAddQuestionVo(QuestionVo questionVo) {
        System.out.println(questionVo);
        Question question = new Question();
        question.setName(questionVo.getName());
        question.setTaskTemplateId(questionVo.getTaskTemplateId());
        question.setSolve(questionVo.getSolve());
        question.setDifficulty(questionVo.getDifficulty());
        question.setId(questionVo.getId());
        Integer res = questionBankMapper.addQuestion(question);
        if(res!=1){
            throw new InsertException("插入数据异常");
        }
        for(Solution solution:questionVo.getSolutions()){
            solution.setQuestionId(question.getId());
            Integer res2 =  questionBankMapper.addSolution(solution);
            if(res2!=1){
                throw new InsertException("插入数据异常");
            }
        }
        List<QuestionVo> questionVoList = this.getTaskTemplateQuestions(questionVo.getTaskTemplateId());
        return questionVoList;

    }

    @Override
    public List<QuestionVo> taskTemplateUpdateQuestionVo(QuestionVo questionVo) {
        System.out.println(questionVo);
        Question question = new Question();
        question.setName(questionVo.getName());
        question.setTaskTemplateId(questionVo.getTaskTemplateId());
        question.setSolve(questionVo.getSolve());
        question.setDifficulty(questionVo.getDifficulty());
        question.setId(questionVo.getId());
        this.updateQuestion(question);
        List<QuestionVo> questionVoList = this.getTaskTemplateQuestions(questionVo.getTaskTemplateId());

        return questionVoList;
    }

    @Override
    public void questionAddSolution(Solution solution){
        Integer res=questionBankMapper.addSolution(solution);
        if(res!=1){
            throw new InsertException("插入数据异常");
        }
    }

    @Override
    public void updateQuestion(Question question){
        Question q = questionBankMapper.getQuestionById(question.getId());
        if(q==null){
            throw new UpdateException("不存在该数据");
        }
        Integer res=questionBankMapper.updateQuestion(question);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void updateSolution(Solution solution){
        Solution s = questionBankMapper.getSolutionById(solution.getId());
        if(s == null){
            throw new UpdateException("不存在该数据");
        }
        Integer res=questionBankMapper.updateSolution(solution);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
    }

    @Override
    public void deleteSolution(Integer solutionId){
        Integer res=questionBankMapper.deleteSolution(solutionId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public void deleteQuestion(Integer questionId){
        List<Solution> solutionList = questionBankMapper.getSolutionByQuestion(questionId);
        System.out.println(solutionList);
        for(Solution solution:solutionList){
           this.deleteSolution(solution.getId());
        }
        Integer res = questionBankMapper.deleteQuestion(questionId);
        if(res!=1){
            throw new DeleteException("删除数据异常");
        }
    }

    @Override
    public List<QuestionVo> getTaskTemplateQuestions(Integer taskTemplateId){
        List<QuestionVo> questionVoList=questionBankMapper.getQuestionVoByTaskTemplate(taskTemplateId);
        for (QuestionVo questionVo:questionVoList){
            List<Solution> solutionList = questionBankMapper.getSolutionByQuestion(questionVo.getId());
            questionVo.setSolutions(solutionList);
        }
        return questionVoList;
    }

}
