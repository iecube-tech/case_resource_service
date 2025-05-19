package com.iecube.community.model.elaborate_md_json_question.service.impl;

import com.iecube.community.model.auth.service.ex.InsertException;
import com.iecube.community.model.auth.service.ex.UpdateException;
import com.iecube.community.model.elaborate_md_json_question.mapper.EMDLabQuestionTemplateMapper;
import com.iecube.community.model.elaborate_md_json_question.qo.PayloadQo;
import com.iecube.community.model.elaborate_md_json_question.service.EMDLabQuestionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EMDLabQuestionTemplateServiceImpl implements EMDLabQuestionTemplateService {

    @Autowired
    private EMDLabQuestionTemplateMapper emdLabQuestionTemplateMapper;

    @Override
    public List<PayloadQo> getQuestionTemplatesByLabId(Long labId) {
        return emdLabQuestionTemplateMapper.getQuestionTemplates(labId);
    }

    @Override
    public PayloadQo addNewQuestionTemplate(PayloadQo payloadQo) {
        int res = emdLabQuestionTemplateMapper.addQuestionTemplate(payloadQo);
        if(res!=1){
            throw new InsertException("新增数据异常");
        }
        return payloadQo;
    }

    @Override
    public PayloadQo updateQuestionTemplate(PayloadQo payloadQo) {
        int res  = emdLabQuestionTemplateMapper.updateQuestionTemplate(payloadQo);
        if(res!=1){
            throw new UpdateException("更新数据异常");
        }
        return emdLabQuestionTemplateMapper.getQuestionTemplate(payloadQo.getId());
    }
}
