package com.iecube.community.model.elaborate_md_json_question.entity;

import com.iecube.community.model.elaborate_md_json_question.question.emdQuestion;
import com.iecube.community.model.elaborate_md_json_question.stuAnswer.StuAnswer;
import com.iecube.community.model.elaborate_md_json_question.table.TableConfig;
import lombok.Data;

@Data
public class Payload {
    private String type;
    private emdQuestion question;
    private TableConfig table;
    private StuAnswer stuAnswer;
}
