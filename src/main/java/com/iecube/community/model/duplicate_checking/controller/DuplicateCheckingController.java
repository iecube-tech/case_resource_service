package com.iecube.community.model.duplicate_checking.controller;

import com.iecube.community.basecontroller.duplicate_checking.DuplicateCheckingBaseController;
import com.iecube.community.model.duplicate_checking.service.DuplicateCheckingService;
import com.iecube.community.model.duplicate_checking.vo.RepetitiveRateVo;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dup")
public class DuplicateCheckingController extends DuplicateCheckingBaseController {

    @Autowired
    private DuplicateCheckingService duplicateCheckingService;

    @GetMapping("/by_task")
    public JsonResult<List> getDupByTaskId(Integer taskId){
        List<RepetitiveRateVo> res  = duplicateCheckingService.getRepetitiveRateByTask(taskId);
        return new JsonResult<>(OK,res);
    }
    @GetMapping("/bi_pstid")
    public JsonResult<List> getDupBYPstId(Integer pstId){
        List<RepetitiveRateVo> res = duplicateCheckingService.getRepetitiveRateByPstId(pstId);
        return new JsonResult<>(OK);
    }

    @PostMapping("/gen")
    public JsonResult<List> genTaskDup(Integer taskId){
        duplicateCheckingService.DuplicateCheckingByTaskId(taskId);
        List<RepetitiveRateVo> res  = duplicateCheckingService.getRepetitiveRateByTask(taskId);
        return new JsonResult<>(OK, res);
    }
}
