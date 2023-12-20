package com.iecube.community.model.question_bank.service;

import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class QuestionBankServiceTests {

    @Autowired
    private QuestionBankService questionBankService;

    @Test
    public void taskTemplateAddQuestion(){
        Question question = new Question();
        question.setTaskTemplateId(23);
        question.setName("请回答以下问题");
        question.setSolve("这道题的解题思路");
        question.setDifficulty(10);
        questionBankService.taskTemplateAddQuestion(question);
    }

    @Test
    public void updateQuestion(){
        Question question = new Question();
        question.setId(3);
        question.setTaskTemplateId(23);
        question.setName("请回答以下问题(更新)");
        question.setSolve("这道题的解题思路(更新)");
        question.setDifficulty(10);
        questionBankService.updateQuestion(question);
    }

    @Test
    public void questionAddSolution(){
        Solution solution = new Solution();
        solution.setQuestionId(3);
        solution.setName("这个是选项1");
        solution.setIsSolution(0);
        questionBankService.questionAddSolution(solution);
    }



    @Test
    public void updateSolution(){
        Solution solution = new Solution();
        solution.setId(8);
        solution.setQuestionId(3);
        solution.setName("这个是选项4");
        solution.setIsSolution(1);
        questionBankService.updateSolution(solution);
    }

    @Test
    public void deleteQuestion(){
        questionBankService.deleteQuestion(2);
    }

    @Test
    public void getTaskTemplateQuestions(){
        System.out.println(questionBankService.getTaskTemplateQuestions(23));
    }
}
