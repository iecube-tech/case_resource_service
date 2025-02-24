package com.iecube.community.model.question_bank.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Solution {
    Integer id;
    Integer questionId;
    String name;
    @JsonProperty("isSolution")
    boolean isSolution;
}
