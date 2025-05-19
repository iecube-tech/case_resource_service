package com.iecube.community.model.elaborate_md_json_question.service;

import com.iecube.community.model.elaborate_md_json_question.qo.PayloadQo;
import com.iecube.community.model.elaborate_md_json_question.question.emdQuestion;

import java.util.List;

public interface EMDLabQuestionTemplateService {

    List<PayloadQo> getQuestionTemplatesByLabId(Long LabId);

   PayloadQo addNewQuestionTemplate(PayloadQo payloadQo);

   PayloadQo updateQuestionTemplate(PayloadQo payloadQo);
}
