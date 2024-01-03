package com.iecube.community.model.question_bank.dto;

import lombok.Data;

@Data
public class PSTQuestionDto {
    Integer id;
    Integer pstId;
    Integer questionId;
    boolean result;
}
