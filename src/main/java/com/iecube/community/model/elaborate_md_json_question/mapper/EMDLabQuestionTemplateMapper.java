package com.iecube.community.model.elaborate_md_json_question.mapper;

import com.iecube.community.model.elaborate_md_json_question.qo.PayloadQo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EMDLabQuestionTemplateMapper {
    List<PayloadQo> getQuestionTemplates(Long labId);

    int addQuestionTemplate(PayloadQo payloadQo);

    int updateQuestionTemplate(PayloadQo payloadQo);

    PayloadQo getQuestionTemplate(Long id);
}
