package com.iecube.community.model.question_bank.vo;

import com.iecube.community.model.question_bank.entity.Solution;
import lombok.Data;

import java.util.List;

@Data
public class QuestionVo {
    Integer id;
    Integer questionId;
    Integer taskTemplateId;
    String name;
    String solve;
    Integer difficulty;
    boolean MultipleChoices;  //多选
    List<Solution> solutions;
    Integer result; // 判断答案的对错
    List<Integer> answer; // 选择的答案
    List<Integer> realAnswer; // 正确答案
}
