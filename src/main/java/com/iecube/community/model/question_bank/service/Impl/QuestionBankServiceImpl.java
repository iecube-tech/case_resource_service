package com.iecube.community.model.question_bank.service.Impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.direction.service.ex.DeleteException;
import com.iecube.community.model.question_bank.dto.PSTQuestionDto;
import com.iecube.community.model.question_bank.dto.PSTQuestionSolutionDto;
import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.mapper.QuestionBankMapper;
import com.iecube.community.model.question_bank.qo.SubmitQo;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.service.ex.CanNotUpdateObjectiveWeighting;
import com.iecube.community.model.question_bank.service.ex.NoQuestionException;
import com.iecube.community.model.question_bank.vo.PSTGW;
import com.iecube.community.model.question_bank.vo.QuestionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Override
    public List<QuestionVo> getQuestions(Integer pstId) {
        //先查看有没有生成的题目
        List<QuestionVo> questionVoList = questionBankMapper.getPstQuestions(pstId);
        if(questionVoList.size()==0){
            // 如果没有生成的重新生成
            this.generatePSTQuestion(pstId);
            questionVoList = questionBankMapper.getPstQuestions(pstId);
        }
        if(questionVoList.size()==0){
            // 重新生成后还是空  则说明没有题目
            throw new NoQuestionException("没有题目");
        }
        for(QuestionVo questionVo : questionVoList){
            List<Solution> solutionList = questionBankMapper.getSolutionByQuestion(questionVo.getQuestionId());
            questionVo.setMultipleChoices(isMultipleChoices(solutionList));
            questionVo.setSolutions(solutionList);
            questionVo.setAnswer(questionBankMapper.getPstQuestionAnswer(questionVo.getId()));
            questionVo.setRealAnswer(questionBankMapper.getPStQuestionRealAnswer(questionVo.getId()));
        }
        return questionVoList;
    }

    public void generatePSTQuestion(Integer pstId){
        // 简易难3个等级的题目  要从每个等级中选取两道
        List<Question> questions1 = questionBankMapper.getQuestion1(pstId);
        List<Question> questions2 = questionBankMapper.getQuestion2(pstId);
        List<Question> questions3 = questionBankMapper.getQuestion3(pstId);
        List<Question> randomQ1= randomQuestion(questions1);
        List<Question> randomQ2= randomQuestion(questions2);
        List<Question> randomQ3= randomQuestion(questions3);
        List<Question> questions = new ArrayList<>();
        questions.addAll(randomQ1);
        questions.addAll(randomQ2);
        questions.addAll(randomQ3);
        for(Question question :questions){
            PSTQuestionDto pstQuestionDto = new PSTQuestionDto();
            pstQuestionDto.setPstId(pstId);
            pstQuestionDto.setQuestionId(question.getId());
            questionBankMapper.addPSTQuestion(pstQuestionDto);
        }
    }

    public List<Question> randomQuestion(List<Question> questionList){
        List<Question> questions = new ArrayList<>();
        if(questionList.size()>2){
            Random random = new Random();
            int a=random.nextInt(questionList.size()-1);
            int b=random.nextInt(questionList.size()-1);
            while (a==b){
                b=random.nextInt(questionList.size()-1);
            }
            questions.add(questionList.get(a));
            questions.add(questionList.get(b));
            return questions;
        }
        return questionList;
    }

    public boolean isMultipleChoices(List<Solution> solutionList){
        int num = 0;
        for(Solution solution : solutionList){
            if (solution.isSolution()==true){
                num+=1;
            }
            solution.setSolution(false);
        }
        if (num > 1){
            return true;
        }
        return false;
    }

    public List<QuestionVo> submitQuestion(List<SubmitQo> submitQoList, Integer pstId){
        for(SubmitQo submitQo: submitQoList){
            for(Integer solutionId: submitQo.getAnswer()){
                //保存学生答案
                PSTQuestionSolutionDto pstQuestionSolutionDto = new PSTQuestionSolutionDto();
                pstQuestionSolutionDto.setPstQuestionId(submitQo.getPstQuestionId());
                pstQuestionSolutionDto.setSolutionId(solutionId);
                questionBankMapper.addPSTQuestionSolution(pstQuestionSolutionDto);
            }
            // 保存学生提交的结果
            if(submitQo.getAnswer().equals(questionBankMapper.getPStQuestionRealAnswer(submitQo.getPstQuestionId()))){
                questionBankMapper.updatePstQuestionResult(submitQo.getPstQuestionId(), 1);
            }else {
                questionBankMapper.updatePstQuestionResult(submitQo.getPstQuestionId(), 0);
            }
        }
        this.computeObjectiveGrade(pstId);
        List<QuestionVo> questionVoList = this.getQuestions(pstId);
        return questionVoList;
    }

    public void computeObjectiveGrade(Integer pstId){
        List<QuestionVo> questionVoList = questionBankMapper.getPstQuestions(pstId);
        int easy = 0;
        int normal = 0;
        int difficult = 0;
        double grade = 0;
        int easeTrue = 0;
        int normalTrue=0;
        int difficultTrue=0;
        // 计算比例 和 正确的个数
        for(QuestionVo questionVo: questionVoList){
            if(questionVo.getDifficulty()==1){
                easy+=1;
                if(questionVo.getResult()==1){
                    easeTrue+=1;
                }
            }
            if(questionVo.getDifficulty()==2){
                normal+=1;
                if(questionVo.getResult()==1){
                    normalTrue+=1;
                }
            }
            if(questionVo.getDifficulty()==3){
                difficult+=1;
                if(questionVo.getResult()==1){
                    difficultTrue+=1;
                }
            }
        }
        // 计算成绩
        if(easy!=0 && normal!=0 &&difficult!=0){
            grade = 30/easy*easeTrue+30/normal*normalTrue+40/difficult*difficultTrue;
        } else if (easy!=0 && normal!=0 &&difficult==0) {
            grade = 50/easy*easeTrue+50/normal*normalTrue;
        }else if (easy!=0 && normal==0 &&difficult!=0){
            grade = 45/easy*easeTrue+55/difficult*difficultTrue;
        } else if (easy==0 && normal!=0 &&difficult!=0) {
            grade = 45/normal*normalTrue+55/difficult*difficultTrue;
        } else if (easy==0 && normal==0 &&difficult!=0) {
            grade = 100/difficult*difficultTrue;
        } else if(easy!=0 && normal==0 &&difficult==0) {
            grade = 100/easy*easeTrue;
        }else if(easy==0 && normal!=0 &&difficult==0) {
            grade = 100/normal*normalTrue;
        }else {
            grade=0;
        }
        questionBankMapper.updatePSTObjectiveGrade(pstId, grade);
    }

    @Override
    public PSTGW getObjectiveGradeAndWeighting(Integer pstId){
        Integer objectiveGrade = questionBankMapper.getObjectiveGrade(pstId);
        Integer objectiveWeighting = questionBankMapper.getObjectiveWeighting(pstId);
        PSTGW pstgw = new PSTGW();
        pstgw.setGrade(objectiveGrade);
        pstgw.setWeighting(objectiveWeighting);
        return pstgw;
    }

    @Override
    public PSTGW updateObjectiveGradeWeighting(Integer pstId, Integer weighting){
        // 需要判断是不是已经有计算过的成绩
        List<Integer> taskObjectiveGrades=questionBankMapper.getTaskObjectiveGrades(pstId);
        System.out.println(taskObjectiveGrades);
        for(Integer grade: taskObjectiveGrades){
            if(grade != null){
                throw new CanNotUpdateObjectiveWeighting("已使用当前权重计算过学生任务/实验的客观题成绩，不可再修改");
            }
        }

        questionBankMapper.updateObjectiveGradeWeighting(pstId, weighting);
        PSTGW pstgw = this.getObjectiveGradeAndWeighting(pstId);
        return pstgw;
    }

}
