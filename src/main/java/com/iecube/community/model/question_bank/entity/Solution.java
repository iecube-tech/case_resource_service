package com.iecube.community.model.question_bank.entity;

import lombok.Data;

@Data
public class Solution {
    Integer id;
    Integer questionId;
    String name;
    Integer isSolution;
}
