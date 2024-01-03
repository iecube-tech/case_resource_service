package com.iecube.community.model.question_bank.qo;

import lombok.Data;

import java.util.List;

@Data
public class SubmitQo {
    Integer pstQuestionId;
    List<Integer> answer;

}
