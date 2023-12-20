package com.iecube.community.model.question_bank.entity;

import lombok.Data;

@Data
public class Question {
    Integer id;
    Integer taskTemplateId;
    String name;
    String solve;
    Integer difficulty;
}
