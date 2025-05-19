package com.iecube.community.model.elaborate_md_json_question.stuAnswer;

import com.iecube.community.model.elaborate_md_json_question.question.ChoiceOption;
import lombok.Data;

import java.util.List;

@Data
public class StuAnswer {
    private List<String> images;
    private String answer;
    private List<ChoiceOption> options;
}
