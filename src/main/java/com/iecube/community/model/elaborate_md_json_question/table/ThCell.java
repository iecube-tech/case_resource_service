package com.iecube.community.model.elaborate_md_json_question.table;

import com.iecube.community.model.elaborate_md_json_question.question.emdQuestion;
import lombok.Data;

@Data
public class ThCell {
    private Long id;
    private Long parentId;
    private String value;
    private boolean colIsNeedInput;
    private boolean colIsAutoGet;
    private emdQuestion question;
}
