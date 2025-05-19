package com.iecube.community.model.elaborate_md_json_question.controller;

import com.iecube.community.basecontroller.BaseController;
import com.iecube.community.model.elaborate_md_json_question.qo.PayloadQo;
import com.iecube.community.model.elaborate_md_json_question.service.EMDLabQuestionTemplateService;
import com.iecube.community.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jqt")
public class EMDLabQuestionTemplateController extends BaseController {

    @Autowired
    private EMDLabQuestionTemplateService emdLabQuestionTemplateService;

    @PostMapping("/add")
    public JsonResult<PayloadQo> add(@RequestBody PayloadQo payloadQo) {
        PayloadQo res = emdLabQuestionTemplateService.addNewQuestionTemplate(payloadQo);
        return new JsonResult<>(OK, res);
    }

    @GetMapping("/{labId}")
    public JsonResult<List<PayloadQo>> get(@PathVariable Long labId) {
        List<PayloadQo> res = emdLabQuestionTemplateService.getQuestionTemplatesByLabId(labId);
        return new JsonResult<>(OK, res);
    }

    @PostMapping("/update")
    public JsonResult<PayloadQo> update(@RequestBody PayloadQo payloadQo) {
        PayloadQo res = emdLabQuestionTemplateService.updateQuestionTemplate(payloadQo);
        return new JsonResult<>(OK, res);
    }
}
