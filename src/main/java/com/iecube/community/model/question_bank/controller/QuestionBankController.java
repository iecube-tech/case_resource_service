package com.iecube.community.model.question_bank.controller;

import com.iecube.community.basecontroller.question_bank.QuestionBankBaseController;
import com.iecube.community.model.question_bank.entity.Question;
import com.iecube.community.model.question_bank.entity.Solution;
import com.iecube.community.model.question_bank.qo.SubmitQo;
import com.iecube.community.model.question_bank.service.QuestionBankService;
import com.iecube.community.model.question_bank.vo.PSTGW;
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

    @PostMapping("/add_qs")
    public JsonResult<List> taskTemplateAddQuestionVo(@RequestBody QuestionVo questionVo){
        List<QuestionVo> questionVoList = questionBankService.taskTemplateAddQuestionVo(questionVo);
        return new JsonResult<>(OK,questionVoList);
    }

    @PostMapping("/update_qs")
    public JsonResult<List> taskTemplateUpdateQuestionVo(@RequestBody QuestionVo questionVo){
        List<QuestionVo> questionVoList= questionBankService.taskTemplateUpdateQuestionVo(questionVo);
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


    /**以下学生端  以及学生端教师端交互
     * */
    @GetMapping("/sq")
    public JsonResult<List> getAQuestion(Integer pstId){
        List<QuestionVo> questionVoList = questionBankService.getQuestions(pstId);
        return new JsonResult<>(OK,questionVoList);
    }

    @PostMapping("/submit")
    public JsonResult<List> submitAnswer(@RequestBody List<SubmitQo> submitQoList, Integer pstId){
        List<QuestionVo> questionVoList = questionBankService.submitQuestion(submitQoList,pstId);
        return new JsonResult<>(OK, questionVoList);
    }

    /**
     * 获取客观题的权重和成绩
     * @param pstId
     * @return
     */
    @GetMapping("/gw")
    public JsonResult<PSTGW> ObjectiveGradeAndWeighting(Integer pstId){
        PSTGW pstGW = questionBankService.getObjectiveGradeAndWeighting(pstId);
        return new JsonResult<>(OK,pstGW);
    }

    /**
     * 更改客观题权重
     */
    @PostMapping("/ogw")
    public JsonResult<PSTGW> ObjectiveWeighting(Integer pstId, Integer weighting){
        PSTGW  pstgw = questionBankService.updateObjectiveGradeWeighting(pstId, weighting);
        return new JsonResult<>(OK,pstgw);
    }
}
