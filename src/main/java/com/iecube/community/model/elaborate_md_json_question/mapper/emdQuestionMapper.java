package com.iecube.community.model.elaborate_md_json_question.mapper;

import com.iecube.community.model.elaborate_md_json_question.question.emdQuestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface emdQuestionMapper {

    int addQuestion(emdQuestion emdQuestion);
}
