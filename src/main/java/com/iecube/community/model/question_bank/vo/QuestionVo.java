package com.iecube.community.model.question_bank.vo;

import com.iecube.community.model.question_bank.entity.Solution;
import lombok.Data;

import java.util.List;

@Data
public class QuestionVo {
    Integer id;
    Integer taskTemplateId;
    String name;
    String solve;
    Integer difficulty;
    List<Solution> solutions;
}
