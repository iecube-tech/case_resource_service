package com.iecube.community.model.question_bank.controller;

import com.iecube.community.basecontroller.question_bank.QuestionBankBaseController;
import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.vo.QuestionVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qb")
public class QuestionBankController extends QuestionBankBaseController {
    @Autowired
    private QuestionBankService questionBankService;

    @GetMapping("/questions")
    public JsonResult<List> getTaskTemplateQuestions(Integer taskTemplateId){
        List<QuestionVo> questionVoList=questionBankService.getTaskTemplateQuestions(taskTemplateId);
        return new JsonResult<>(OK, questionVoList);
    }

    @PostMapping("/add_question")
    public JsonResult<Void> taskTemplateAddQuestion(@RequestBody Question question){
        questionBankService.taskTemplateAddQuestion(question);
        return new JsonResult<>(OK);
    }

    @PostMapping("/update_question")
    public JsonResult<Void> updateQuestion(@RequestBody Question question){
        questionBankService.updateQuestion(question);
        return new JsonResult<>(OK);
    }

    @PostMapping("/add_solution")
    public JsonResult<Void> questionAddSolution(@RequestBody Solution solution){
        questionBankService.questionAddSolution(solution);
        return new JsonResult<>(OK);
    }

    @PostMapping("/update_solution")
    public JsonResult<Void> updateSolution(@RequestBody Solution solution){
        questionBankService.updateSolution(solution);
        return new JsonResult<>(OK);
    }

    @DeleteMapping("/del_solution")
    public JsonResult<Void> deleteSolution(Integer solutionId){
        questionBankService.deleteSolution(solutionId);
        return new JsonResult<>(OK);
    }

    @DeleteMapping("/del_question")
    public JsonResult<Void> deleteQuestion(Integer questionId){
        questionBankService.deleteQuestion(questionId);
        return new JsonResult<>(OK);
    }

}
